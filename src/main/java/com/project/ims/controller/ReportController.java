package com.project.ims.controller;

import com.project.ims.model.dto.PeriodStatisticsDTO;
import com.project.ims.model.dto.ProductStatisticsDTO;
import com.project.ims.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Year;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/statistics-by-period")
    public ResponseEntity<PeriodStatisticsDTO> getStatisticsByPeriod(
            @RequestParam String type,
            @RequestParam String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        if (year == null) {
            year = Year.now().getValue();
        }
        
        PeriodStatisticsDTO statistics = reportService.getStatisticsByPeriod(type, period, year, month);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/statistics-by-product")
    public ResponseEntity<ProductStatisticsDTO> getStatisticsByProduct(
            @RequestParam String type,
            @RequestParam int productId,
            @RequestParam String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        if (year == null) {
            year = Year.now().getValue();
        }
        
        ProductStatisticsDTO statistics = reportService.getStatisticsByProduct(type, productId, period, year, month);
        return ResponseEntity.ok(statistics);
    }
}