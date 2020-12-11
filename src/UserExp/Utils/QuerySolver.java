package UserExp.Utils;

import org.apache.log4j.Logger;
import jdbcutils.JdbcUtil;

import java.sql.*;
import java.util.*;

import static UserExp.Utils.GlobalStore.*;

public class QuerySolver
{
    private static Logger log = Logger.getLogger(QuerySolver.class);

    public static JdbcUtil db = new JdbcUtil();

    public static List<String> getCategoriesAccordingAnnotationId(Integer id)
    {
        if(null == id) { return null; }
        return getACategory(id);
    }

    public static List<Map<String, Object>> getTemplatesAccordingAnnotationId(Integer id) // template, template_slots
    {
        if(null == id) { return null; }
        return getATemplate(id);
    }

    public static List<Map<String, Object>> getShortansAccordingBGID(Integer id) // "question", "answer", "background", "parsing", "checkpoint", "material"
    {
        if(null == id) { return null; }
        return getShortans(id);
    }

    public static byte[] getImagePic(Integer imgid)
    {
        if(null == imgid) { return null; }
        String sql = String.format("select image_data from image where id=%d;", imgid);

        byte[] result = null;

        try
        {
            Connection conn = db.getNewConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while(rs.next())
            {
                result = rs.getBytes("image_data");
            }

            conn.close(); rs.close(); st.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        return result;
    }

    public static Map<String, Object> getJSONInstance(Integer imageid) // AnnoImage, Templates(List of Map<String, Object>), Categories(List of String), shortAns(JSONArray)
    {
        if(null == imageid) { return null; }
        // Initialize
        AnnoImage ai = getAnnoImageAccordingImageId(imageid);
        Map<String, Object> finalResult = new HashMap<>(); finalResult.clear();
        for(String key : ai.content.keySet()) { finalResult.put(key, ai.content.get(key)); }
        //for(String key : finalResult.keySet()) log.info(String.format("KEY : %s, VALUE : %s, TYPE : %s", key, finalResult.get(key).toString(), finalResult.get(key).getClass().toString()));

        // Add Teamplates and Categories
        Integer annotation_id = (Integer)finalResult.get("annotationId");
        //log.info(String.format("Annotation ID : %d !", annotation_id));
        finalResult.put("Templates", getTemplatesAccordingAnnotationId(annotation_id));
        finalResult.put("Categories", getCategoriesAccordingAnnotationId(annotation_id));

        // Add Short Answer
        Integer BGID = (Integer)finalResult.get("backgroundId");
        //log.info(String.format("BackGround ID : %d !", BGID));
        finalResult.put("shortAns", getShortansAccordingBGID(BGID));

        return finalResult;
    }

    public static List<Map<String, Object>> solveAccordingAnnotator(String annotator)
    {
        String condition1 = "";
        String condition2 = "";
        if(!annotator.equals("*"))
        {
            condition1 = String.format("where username = '%s'", annotator);
            condition2 = String.format("where annotator = '%s'", annotator);
        }
        String sql = String.format("select * from annotations %s and image_id not in (select image_id from image_shortans_annotation_check %s) limit 1;", condition1, condition2);

        List<Map<String, Object>> result = new ArrayList<>(); result.clear();
        List<Integer> images = new ArrayList<>(); images.clear();

        try
        {
            Connection conn = db.getNewConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while(rs.next())
            {
                Integer imageid = rs.getInt("image_id");
                images.add(imageid);
            }

            rs.close(); st.close(); conn.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        log.info(String.format("User : %s, Total Image Number : %d !", annotator, images.size()));

        for(Integer image : images) { result.add(getJSONInstance(image)); }

        return result;
    }

    public static Boolean writeDatabase(Map<String, Object> obj)
    {
        List<String> stringField = Arrays.asList("annotator", "evaluator", "annotation_text", "slot");
        List<String> integerField = Arrays.asList("image_id", "annotation_id", "template_id", "objectivity", "adequacy", "completeness");
        List<String> fields = Arrays.asList("image_id", "annotation_id", "annotator", "evaluator", "annotation_text", "template_id", "slot", "objectivity", "adequacy", "completeness");

        String sql = "INSERT INTO image_shortans_annotation_check (image_id, annotation_id, annotator, evaluator, annotation_text, template_id, slot, objectivity, adequacy, completeness)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try
        {
            Connection conn = db.getNewConnection();
            PreparedStatement pst = conn.prepareStatement(sql);
            for(Integer i = 0; i < fields.size(); ++ i)
            {
                Integer pos = i + 1;
                String key = fields.get(i);
                String val = obj.get(key).toString();
                if(integerField.contains(key))
                {
                    pst.setInt(pos, Integer.parseInt(val));
                }else
                {
                    pst.setString(pos, val);
                }
            }
            pst.execute();

            pst.close(); conn.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }
}
