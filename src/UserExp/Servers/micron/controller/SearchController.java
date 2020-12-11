package UserExp.Servers.micron.controller;

import UserExp.Utils.QuerySolver;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.*;

import static UserExp.Utils.GlobalStore.getAnnoImageAccordingImageId;
import static UserExp.Utils.QuerySolver.*;

@RestController
@CrossOrigin
public class SearchController {

    private static Logger log = Logger.getLogger(SearchController.class);

    @RequestMapping("/test")
    public void forTest(int id, String di) throws SQLException {
        System.out.println(id);
        System.out.println(di);
    }

    @RequestMapping("/jsonTest")
    public void jsonTest(@RequestBody Map<String, Object> obj)
    {
        System.out.println(obj.toString());
        for(String key : obj.keySet())
        {
            System.out.println(String.format("%s : %s", key, obj.get(key).toString()));
        }
    }

    @RequestMapping("/getInfo")
    public List<Map<String, Object>> getInfo(String username)
    {
        if(username.equals("")) { username = "*"; }
        log.info(String.format("Get Info ! Username : %s", username));
        return solveAccordingAnnotator(username);
    }

    @RequestMapping("/mapTest")
    public Map<String, Object> mapTest(String s)
    {
        Map<String, Object> result = new HashMap<>(); result.clear();
        result.put("s", s);
        result.put("a", 1);
        return result;
    }

    @RequestMapping("/mapListTest")
    public List<Map<String, Object>> mapListTest(String s)
    {
        List<Map<String, Object>> result = new ArrayList<>(); result.clear();
        for(Integer i = 0; i < 5; ++ i)
        {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put("s", s);
            tmp.put("v", i);
            result.add(tmp);
        }
        Map<String, Object> addi = new HashMap<>(); addi.clear();
        addi.put("s", s); addi.put("list", Arrays.asList(1, 2, 3));
        result.add(addi);
        return result;
    }

    @RequestMapping("/imageAnnotate")
    public void image(HttpServletResponse response, int imgId) throws SQLException, IOException
    {
        byte[] buffer = getImagePic(imgId);
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        os.write(buffer); os.flush(); os.close();
    }

    @RequestMapping("/addInfo")
    public Map<String, Object> addInfo(@RequestBody Map<String, Object> obj)
    {
        Map<String, Object> result = new HashMap<>(); result.clear();
        List<String> fields = Arrays.asList("image_id", "annotation_id", "annotator", "evaluator", "annotation_text", "objectivity", "adequacy", "completeness");
        for(String key : fields)
        {
            if(!obj.containsKey(key))
            {
                String msg = String.format("addInfo do not have parameter : %s !", key);
                log.error(msg);
                result.put("ReturnCode", "Fail");
                result.put("msg", msg);
                return result;
            }
        }
        if(!obj.containsKey("template_id_list"))
        {
            String msg = String.format("addInfo do not have parameter : %s !", "template_id_list");
            log.error(msg);
            result.put("ReturnCode", "Fail");
            result.put("msg", msg);
            return result;
        }
        if(!obj.containsKey("slot_list"))
        {
            String msg = String.format("addInfo do not have parameter : %s !", "slot_list");
            log.error(msg);
            result.put("ReturnCode", "Fail");
            result.put("msg", msg);
            return result;
        }
        List<Object> tid_list = (List<Object>)obj.get("template_id_list");
        List<Object> slot_list = (List<Object>)obj.get("slot_list");
        if(tid_list.size() != slot_list.size())
        {
            String msg = String.format("addInfo Error! template_id_list length != slot_list length");
            log.error(msg);
            result.put("ReturnCode", "Fail");
            result.put("msg", msg);
            return result;
        }

        for(Integer i = 0; i < tid_list.size(); ++ i)
        {
            Integer tid = (Integer)tid_list.get(i);
            String slot = (String)slot_list.get(i);
            Map<String, Object> tmp = new HashMap<>(); tmp.clear();
            for(String key : fields) { tmp.put(key, obj.get(key)); }
            tmp.put("template_id", tid);
            tmp.put("slot", slot);
            if(!writeDatabase(tmp))
            {
                String msg = String.format("writeDatabase Fail ! tid : %d, slot : %s", tid, slot);
                log.error(msg);
                result.put("ReturnCode", "Fail");
                result.put("msg", msg);
                return result;
            }
        }
        result.put("ReturnCode", "Success");
        result.put("msg", "Congratulations!");
        return result;
    }
}
