package org.sakaiproject.wicket.test.app.impl;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.WorkbookUtil;

import org.sakaiproject.wicket.test.app.api.ExportService;
import org.sakaiproject.wicket.test.app.api.model.GradingInfo;

public class ExportServiceImpl implements ExportService {
    private HSSFWorkbook wb;
    private HSSFSheet sheet;

    public void init() {
        wb = new HSSFWorkbook();
        sheet = wb.createSheet(WorkbookUtil.createSafeSheetName("E:\\pole.xlsx"));
    }

    @Override
    public String exportGradings(List<GradingInfo> gradings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
