package UserExp.Utils;

import jdbcutils.JdbcUtil;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

public class GlobalStore
{
    private static Logger log = Logger.getLogger(GlobalStore.class);

    public static JdbcUtil db = new JdbcUtil();

    public static Map<Integer, AnnoImage> imageid2content = new HashMap<>();

    public static List<String> getMaterials(String idstr)
    {
        List<String> mats = new ArrayList<>(); mats.clear();
        if(null == idstr) { return mats; }
        String[] ids = idstr.split(";");
        for(String id : ids) { mats.add(getMaterial(Integer.parseInt(id))); }
        return mats;
    }

    public static void readImage()
    {
        List<String> addtionalInt = Arrays.asList("questionError", "unableAnnotation", "uncertain", "duplicate", "same", "unuseful", "sort_id");

        try
        {
            Connection conn = db.getNewConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM image;");
            while(rs.next())
            {
                Integer imageid = rs.getInt("id");
                if(!imageid2content.containsKey(imageid))
                {
                    AnnoImage ai = new AnnoImage();
                    imageid2content.put(imageid, ai);
                }
                AnnoImage ai = imageid2content.get(imageid);
                for(String key : addtionalInt) { ai.addProp(key, rs.getInt(key)); }
                Integer bgid = rs.getInt("background_id");
                ai.addProp("backgroundId", bgid);
                imageid2content.put(imageid, ai);
            }

            rs.close(); st.close(); conn.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    public static void readAnnotations()
    {
        try
        {
            Connection conn = db.getNewConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM annotations;");
            while(rs.next())
            {
                Integer annotationid = rs.getInt("id");
                String annotationtext = rs.getString("annotation_text");
                Integer imageid = rs.getInt("image_id");
                String username = rs.getString("username");
                if(!imageid2content.containsKey(imageid))
                {
                    AnnoImage ai = new AnnoImage();
                    imageid2content.put(imageid, ai);
                }
                AnnoImage ai = imageid2content.get(imageid);
                ai.addProp("annotationId", annotationid);
                ai.addProp("annotationText", annotationtext);
                ai.addProp("imageId", imageid);
                ai.addProp("annotator", username);
                imageid2content.put(imageid, ai);
            }

            rs.close(); st.close(); conn.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    public static Map<Integer, String> id2categories = new HashMap<>();
    public static void readCategory()
    {
        id2categories.clear();
        try
        {
            Connection conn = db.getNewConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM categories;");
            while(rs.next())
            {
                Integer id = rs.getInt("id");
                String label = rs.getString("label");
                id2categories.put(id, label);
            }

            rs.close(); st.close(); conn.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        log.info(String.format("Read %s Success ! Set Size : %d", "Category", id2categories.size()));

    }

    public static Map<Integer, String> id2material = new HashMap<>();
    public static void readMaterial()
    {
        id2material.clear();
        try
        {
            Connection conn = db.getNewConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM shortansmaterial;");
            while(rs.next())
            {
                Integer id = rs.getInt("id");
                String material = rs.getString("material");
                id2material.put(id, material);
            }

            rs.close(); st.close(); conn.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        log.info(String.format("Read %s Success ! Set Size : %d", "Material", id2material.size()));

    }

    public static Map<Integer, Map<String, Object>> id2templates = new HashMap<>(); // category_id, text
    public static void readTemplates()
    {
        id2templates.clear();
        try
        {
            Connection conn = db.getNewConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM templates;");
            while(rs.next())
            {
                Map<String, Object> obj = new HashMap<>();
                Integer id = rs.getInt("id");

                Integer cat_id = rs.getInt("category_id");
                String text = rs.getString("text");
                obj.put("category_id", cat_id);
                obj.put("text", text);

                id2templates.put(id, obj);
            }

            rs.close(); st.close(); conn.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        log.info(String.format("Read %s Success ! Set Size : %d", "Template", id2templates.size()));

    }

    public static Map<Integer, List<Map<String, Object>>> id2Atemplates = new HashMap<>(); // category_id, text
    public static void readATemplates()
    {
        id2Atemplates.clear();
        try
        {
            Connection conn = db.getNewConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM annotation_templates;");
            while(rs.next())
            {
                Map<String, Object> obj = new HashMap<>();
                Integer tid = rs.getInt("template_id");
                obj.put("template", getTemplate(tid));
                obj.put("template_slots", rs.getString("template_slots"));
                obj.put("template_id", tid);
                Integer aid = rs.getInt("annotation_id");
                if(!id2Atemplates.containsKey(aid))
                {
                    List<Map<String, Object>> tmp = new ArrayList<>();
                    tmp.clear(); id2Atemplates.put(aid, tmp);
                }
                List<Map<String, Object>> tmp = id2Atemplates.get(aid);
                tmp.add(obj); id2Atemplates.put(aid, tmp);
            }

            rs.close(); st.close(); conn.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        log.info(String.format("Read %s Success ! Set Size : %d", "ATemplate", id2Atemplates.size()));

    }

    public static Map<Integer, List<String>> id2Acategories = new HashMap<>(); // category_id, text
    public static void readACategory()
    {
        id2Acategories.clear();
        try
        {
            Connection conn = db.getNewConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM annotation_category;");
            while(rs.next())
            {
                String cat = getCategory(rs.getInt("category_id"));
                Integer aid = rs.getInt("annotation_id");
                if(!id2Acategories.containsKey(aid))
                {
                    List<String> tmp = new ArrayList<>();
                    tmp.clear(); id2Acategories.put(aid, tmp);
                }
                List<String> tmp = id2Acategories.get(aid);
                tmp.add(cat); id2Acategories.put(aid, tmp);
            }

            rs.close(); st.close(); conn.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        log.info(String.format("Read %s Success ! Set Size : %d", "ACategory", id2Acategories.size()));

    }

    public static void joinAnnoImage()
    {
        imageid2content.clear();

        readAnnotations(); readImage();
    }

    public static Map<Integer, List<Map<String, Object>>> id2shortans = new HashMap<>(); // category_id, text
    public static void readShortans()
    {
        id2shortans.clear();
        List<String> textField = Arrays.asList("question", "answer", "background", "parsing", "checkpoint");
        try
        {
            Connection conn = db.getNewConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM image_shortans;");
            while(rs.next())
            {
                Map<String, Object> obj = new HashMap<>();
                for(String key : textField) { obj.put(key, rs.getString(key)); }
                obj.put("material", getMaterials(rs.getString("material_id")));
                Integer bgid = rs.getInt("background_id");
                if(!id2shortans.containsKey(bgid))
                {
                    List<Map<String, Object>> tmp = new ArrayList<>();
                    tmp.clear(); id2shortans.put(bgid, tmp);
                }
                List<Map<String, Object>> tmp = id2shortans.get(bgid);
                tmp.add(obj); id2shortans.put(bgid, tmp);
            }

            rs.close(); st.close(); conn.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        log.info(String.format("Read %s Success ! Set Size : %d", "Shortans", id2shortans.size()));

    }

    public static AnnoImage getAnnoImageAccordingImageId(Integer id) { return imageid2content.get(id); }

    public static String getMaterial(Integer id) { return id2material.get(id); }

    public static String getCategory(Integer id) { return id2categories.get(id); }

    public static Map<String, Object> getTemplate(Integer id) { return id2templates.get(id); }

    public static List<Map<String, Object>> getATemplate(Integer id) { return id2Atemplates.get(id); }

    public static List<String> getACategory(Integer id) { return id2Acategories.get(id); }

    public static List<Map<String, Object>> getShortans(Integer id) { return id2shortans.get(id); }

    public static void Entry()
    {
        joinAnnoImage();
        readCategory(); readTemplates(); readMaterial();
        readATemplates(); readACategory(); readShortans();
    }
}
