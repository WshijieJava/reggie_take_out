package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 书籍管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
@Api(tags = "书籍管理接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增书籍")
    public R<String> save(
            @ApiParam(name = "dishDto",required = true,value = "书籍信息")
            @RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @ApiOperation(value = "书籍分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", required = true, value = "页号"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页面大小")
    })
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /**
     * 根据id查询书籍信息和对应的标签信息
     * @param id
     * @return
     */
    @ApiOperation(value = "据id查询书籍信息和对应的标签信息")
    @GetMapping("/{id}")
    public R<DishDto> get(
            @ApiParam(name = "id",required = true,value = "书籍id")
            @PathVariable Long id) {

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    /**
     * 修改书籍信息
     * @param dishDto
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改书籍信息")
    public R<String> update(
            @ApiParam(name = "dishDto", required = true, value = "书籍信息")
            @RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }


    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }*/


    @ApiOperation("根据分类查询相关书籍信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "dish", required = true, value = "分类信息")
//    })
    @GetMapping("/list")
    @Cacheable(value = "dishCache",key = "#dish.categoryId + '_' + #dish.status")
    public R<List<DishDto>> list(
            @ApiParam(name = "dish", required = true, value = "分类信息")
            Dish dish) {
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }


    @ApiOperation(value = "根据id修改书籍状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status", required = true, value = "书籍状态")
    })
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status") Integer status, @RequestParam("ids") List<Long> ids) {
        log.info("status:{},停售起售的ids：{}", status, ids);
        //通过ids批量获取dish 菜品
        List<Dish> dishes = dishService.listByIds(ids);
        //利用stream流，设置dish的status字段为  传过来的status值
        dishes = dishes.stream().map(s -> {
            s.setStatus(status);
            return s;
        }).collect(Collectors.toList());
        //批量修改
        boolean b = dishService.updateBatchById(dishes);


        return b ? R.success("操作成功") : R.error("操作失败");
    }

////真删除，数据库直接删除
//    @DeleteMapping
//    public R<String> delete(@RequestParam("ids") List<Long> ids){
//        log.info("删除的ids：{}",ids);
//
//        boolean deleteStatus = dishService.removeByIds(ids);
//
//
//        return deleteStatus?R.success("删除成功"):R.error("删除失败");
//    }


    //方式 2：逻辑删除，数据库不删除，只改变状态
    @ApiOperation(value = "根据id删除书籍")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", required = true, value = "根据id对书籍进行逻辑删除")
    })
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids) {
        log.info("删除的ids：{}", ids);
//        可以使用逻辑，删除，打开Dish中的逻辑删除字段
        boolean deleteStatus = dishService.removeByIds(ids);
        return deleteStatus ? R.success("删除成功") : R.error("删除失败");
    }
}
