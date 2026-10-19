
package com.hrms.controller;

import com.hrms.entity.Holiday;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/holidays")
public class HolidayController {

    @GetMapping("/test")
    public String testHoliday() {
        return "Holiday module is working";
    }
}
