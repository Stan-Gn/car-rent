package com.app.carrent.controller.modelSaver;

import javassist.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageAndPageNumbersModelSaver {
    public void saveToModel(ModelAndView modelAndView, String pageAttributeName, Page page, int currentPage) throws NotFoundException {
        if (currentPage > page.getTotalPages())
            throw new NotFoundException("Page not found");

        modelAndView.addObject(pageAttributeName, page);

        int totalPages = page.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
    }
}
