package com.wisecoders.dbschema.mongodb;

import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Copyright Wise Coders GmbH. The MongoDB JDBC driver is build to be used with  <a href="https://dbschema.com">DbSchema Database Designer</a>
 * Free to use by everyone, code modifications allowed only to the  <a href="https://github.com/wise-coders/mongodb-jdbc-driver">public repository</a>
 */

public class Util {

    public static Object getByPath(Document document, String path ){
        String[] pathEls = path.split("\\.");
        Object cursor = document;
        int idx = 0;
        while ( idx < pathEls.length && cursor instanceof Map){
            cursor = ((Map)cursor).get( pathEls[idx] );
            idx++;
        }
        return ( idx == pathEls.length) ? cursor : null;
    }

    public static int getJavaType( String bsonType ){
        switch ( bsonType ){
            case "int": return Types.INTEGER;
            case "double": return Types.DOUBLE;
            case "array": return Types.ARRAY;
            case "objectId": return Types.ROWID;
            case "bool": return Types.BOOLEAN;
            case "date": return Types.DATE;
            case "null": return Types.NULL;
            case "dbPointer": return Types.DATALINK;
            case "javascript": return Types.JAVA_OBJECT;
            case "timestamp": return Types.TIMESTAMP;
            case "long": return Types.BIGINT;
            case "decimal": return Types.DECIMAL;
            case "minKey":
            case "maxKey": return Types.SMALLINT;
            case "regex":
            case "javascriptWithScope":
            case "string":
            case "symbol":
            default: return Types.VARCHAR;
        }
    }


    private static final Pattern PATTERN_NUMBER = Pattern.compile("\\d+");

    public static boolean allKeysAreNumbers( Map map ){
        if ( map.isEmpty() ) return false;
        for( Object key : map.keySet()){
            boolean isNumber = key instanceof Number || ( key instanceof String && PATTERN_NUMBER.matcher( (String)key).matches() );
            if ( !isNumber ) return false;
        }
        return true;
    }

    public static int getJavaType( Object value ){
        if ( value instanceof Integer ) return java.sql.Types.INTEGER;
        else if ( value instanceof Timestamp) return java.sql.Types.TIMESTAMP;
        else if ( value instanceof Date) return java.sql.Types.DATE;
        else if ( value instanceof Double ) return java.sql.Types.DOUBLE;
        return java.sql.Types.VARCHAR;
    }

    public static Class getListElementsClass(Object obj){
        if ( obj instanceof List){
            List list = (List)obj;
            Class cls = null;
            for ( Object val : list ){
                Class _cls = null;
                if ( val instanceof Map ) _cls = Map.class;
                else if ( val instanceof Integer ) _cls = Integer.class;
                else if ( val instanceof Double ) _cls = Double.class;
                else if ( val instanceof Long ) _cls = Long.class;
                else if ( val instanceof Boolean ) _cls = Boolean.class;
                else if ( val instanceof Date ) _cls = Date.class;
                else if ( val instanceof String ) _cls = String.class;
                if ( cls == null ) cls = _cls;
                else if ( cls != _cls ) cls = Object.class;
            }
            return cls;
        }
        return null;
    }

    public static String getBsonType(Document bsonDefinition){
        Object bsonTypeObj = bsonDefinition.get("bsonType");
        if ( bsonTypeObj instanceof List){
            return String.valueOf(((List)bsonTypeObj).get(0));
        } else {
            return String.valueOf(bsonTypeObj);
        }
    }

    public static String readStringFromInputStream(InputStream is) throws IOException {
        if ( is == null ){
            throw new IOException("Got empty Input Stream");
        }
        final BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        final StringBuilder sb = new StringBuilder();
        String str;
        while (null != ((str = in.readLine()))) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(str);
        }
        in.close();
        return sb.toString();
    }
}
