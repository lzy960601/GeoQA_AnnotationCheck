package UserExp.Utils;

import java.util.HashMap;
import java.util.Map;

public class AnnoImage
{
    /*
    public Integer imageId;
    public Integer annotationId;
    public String annotator;
    public String annotationText;
    public Integer backgroundId;
    public Integer sort_id;
    public String imageText;
    public Integer questionError;
    public Integer unableAnnotation;
    public Integer uncertain;
    public Integer duplicate;
    public Integer same;
    public Integer unuseful;
     */

    public Map<String, Object> content;

    public AnnoImage()
    {
        content = new HashMap<>();
    }

    public void addProp(String key, Object value) { content.put(key, value); }

    public void show(){ for(String key : content.keySet()) { System.out.println(String.format("%s : %s", key, content.get(key).toString())); } }
}
