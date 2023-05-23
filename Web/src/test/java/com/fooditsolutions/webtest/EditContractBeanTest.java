package com.fooditsolutions.webtest;
import com.fooditsolutions.util.enums.Source;
import com.fooditsolutions.util.model.Index;
import com.fooditsolutions.web.bean.EditContractBean;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditContractBeanTest {

    @Test
    public void testUpdateCPI() {
        //Arrange
        EditContractBean editContractBean=new EditContractBean();

        Calendar calendar = Calendar.getInstance();

        // Set the desired date
        calendar.set(Calendar.YEAR, 2022);
        calendar.set(Calendar.MONTH, Calendar.APRIL);
        calendar.set(Calendar.DAY_OF_MONTH, 3);

        int baseYear = 1996;

        Index index1 = new Index(2022, "mei 2022", "1996 = 100", new BigDecimal("170.22"), Source.FILE);
        Index index2 = new Index(2022, "april 2022", "1996 = 100", new BigDecimal("168.93"), Source.FILE);
        Index index3 = new Index(2022, "maart 2022", "1996 = 100", new BigDecimal("168.37"), Source.FILE);

        Index[] cpis=new Index[3];
        cpis[0]=index1;
        cpis[1]=index2;
        cpis[2]=index3;

        editContractBean.setCpis(cpis);

        //Act
        BigDecimal result = editContractBean.updateCPI(calendar.getTime(), baseYear);

        //Assert
        Assert.assertEquals(new BigDecimal("168.37"), result);
    }




}
