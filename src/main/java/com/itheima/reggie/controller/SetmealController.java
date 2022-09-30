package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Api(tags = "套餐管理接口")
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    CacheManager cacheManager;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @ApiOperation(value = "新增套餐")
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)//清除setmealCache名称下，所有的缓存数据
    public R<String> save(
            @ApiParam(name = "setmealDto",required = true,value = "套餐信息")
            @RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @ApiOperation(value = "套餐分页查询")
    @GetMapping("/page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",required = true,value = "页号"),
            @ApiImplicitParam(name = "pageSize",required = true,value = "页面大小"),
            @ApiImplicitParam(name = "name", required = true, value = "查询信息")
    })
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }


    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除套餐")
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)//清除setmealCache名称下，所有的缓存数据
    public R<String> delete(
            @ApiParam(name = "ids",required = true, value = "套餐id")
            @RequestParam List<Long> ids) {
        log.info("ids:{}", ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }


    /**
     * 将分类id+状态值作为缓存的key值，下一次
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "根据条件查询套餐数据")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(
            @ApiParam(name = "setmeal", required = true, value = "查询条件")
            Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 如果是多个数据，用集合接收，必须使用,@RequestParam("ids")注解
     *
     * @param status
     * @param ids
     * @return
     */
    @ApiOperation(value = "查询套餐状态")
    @PostMapping("/status/{status}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status",required = true,value = "套餐状态"),
            @ApiImplicitParam(name = "ids", required = true,value = "套餐id")
    })
    public R<String> updateStatus(
            @PathVariable Integer status, @RequestParam("ids") List<Long> ids) {

        log.info("updateStatus：ids：{},status:{}", ids, status);
        //1. 通过传过来的ids 获得所有的菜品
        //修改套餐状态只需要修改Setmeal表，因此不需要多表操作
        List<Setmeal> setmeals = setmealService.listByIds(ids);
        //2. 遍历菜品集合，依次设置状态值
        for (Setmeal setmeal : setmeals) {
            setmeal.setStatus(status);
        }
        //3. 修改状态
        boolean b = setmealService.updateBatchById(setmeals);

        return b ? R.success("操作成功") : R.error("操作失败");
    }


//    @GetMapping("/{id}")
//    @ApiOperation(value = "获取套餐")
//    public R<Setmeal> getSetmealById(
//            @ApiParam(name = "id",required = true,value = "套餐id")
//            @PathVariable Long id){
//        System.out.println(id);
//        Setmeal setmeal = setmealService.getById(id);
//        return R.success(setmeal);
//    }

    /**
     * 根据id查询套餐信息
     *(套餐信息的回显)
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        log.info("根据id查询套餐信息:{}", id);
        // 调用service执行查询。、
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping()
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithFlavor(setmealDto);
        return R.success("修改套餐成功");
    }


}
