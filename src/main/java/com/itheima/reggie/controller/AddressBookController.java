package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@Api(tags = "用户地址管理接口")
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     */
    @ApiOperation(value = "保存用户地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressBook",required = true,value = "地址信息实体")
    })
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 修改地址
     */
    @PutMapping
    @ApiOperation(value = "更新用户地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressBook",required = true,value = "地址信息实体")
    })
    public R<AddressBook> update(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    @ApiOperation(value = "设置默认用户地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressBook",required = true,value = "地址信息实体")
    })
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        // 将该用户所有的地址都设置为非默认
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        // 单独设置为1，保障当前用户有且只有一个默认地址
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询用户地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",required = true,value = "地址id")
    })
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    @ApiOperation(value = "查询默认用户地址")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询指定用户的全部地址")
    public R<List<AddressBook>> list(@ApiParam(name = " addressBook" ,required = true,value = "地址信息实体") AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }

    /**
     * 删除地址
     */
    @DeleteMapping
    @ApiOperation(value = "删除多个地址地址")
    public R<String> deleteById(
            @ApiParam(name = "ids" , required = true, value = "待删除的地址的id列表")
            @RequestParam("ids") List<Long> ids) {

        boolean isDelete = addressBookService.removeByIds(ids);

        return isDelete ? R.success("删除成功") : R.error("删除失败");
    }


}
