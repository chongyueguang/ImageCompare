package com.company.util;
import com.company.model.ResultInfoModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {
    public static ResultInfoModel parseResult(String json){

        ObjectMapper om = new ObjectMapper();
        ResultInfoModel resultInfo = null;
        try {
            resultInfo = om.readValue(json, ResultInfoModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultInfo;
    }

}
