package com.bid90.util;

import javax.ws.rs.BadRequestException;
import java.util.Collections;
import java.util.List;

public class Pagination <T>{

    public Integer items;
    public Integer pageIndex;
    public Integer itemsPage;
    public List<T> data;

    public Pagination(List<T> data, Integer pageIndex, Integer itemsPage, Integer items) {
        this.items = items;
        this.pageIndex = pageIndex;
        this.itemsPage = itemsPage;
        this.data = data;
    }

    public static void paginationValidation(Integer pageIndex, Integer pageSize) throws BadRequestException{
        if (pageIndex <= 0 || pageSize <= 0) {
            throw new BadRequestException("pageIndex and pageSize mast be > 0");
        }
        if(pageSize > 100){
            throw new BadRequestException("pageSize mast be from 1 to 100");
        }

    }

    public static <T> Pagination<T> paginate(List<T> list, Integer pageIndex, Integer pageSize) {

        Pagination.paginationValidation(pageIndex, pageSize);

        int fromIndex = (pageIndex - 1) * pageSize;
        if (list == null || list.size() <= fromIndex) {
            return new Pagination<>(Collections.emptyList(), pageIndex, pageSize, 0);

        }
        return new Pagination<>(list.subList(fromIndex, Math.min(fromIndex + pageSize, list.size())), pageIndex,
                pageSize, list.size());

    }

}

