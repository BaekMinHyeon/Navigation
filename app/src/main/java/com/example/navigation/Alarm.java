package com.example.navigation;

import android.util.Log;

import com.skt.Tmap.TMapPoint;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class Alarm {

    public ArrayList<TMapPoint> saveRoutePoint;

    public Alarm(ArrayList<TMapPoint> saveRoutePoint){
        this.saveRoutePoint = saveRoutePoint;
    }

    public boolean accident() throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://www.utic.go.kr/guide/imsOpenData.do"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("key","UTF-8") + "=joqQEZdJV6jtlHRXFxCXZZ15xTpzfxMQIQmcK0ElMAe3deCmWv83I8Z93MoVVs"); /*Service Key*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "text/xml;charset=UTF-8");
        Log.e("Response code: ", conn.getResponseCode()+"");
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        Log.e("민현이", sb.toString());

        ArrayList<Object> list_x = new ArrayList<Object>();
        ArrayList<Object> list_y = new ArrayList<Object>();
        List<HashMap<String, String>> list = null;
        try {
            list = getResultMap(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(Map<String,String> tmpMap : list) {
            Log.e("길이", String.valueOf(list.size()));
            list_x.add(tmpMap.get("locationDataX"));
            list_y.add(tmpMap.get("locationDataY"));
        }
        for(int i = 0; i < list_x.size(); i++){
            Log.e("x좌표", list_x.get(i).toString());
            Log.e("y좌표", list_y.get(i).toString());
        }

        return passAccident();
    }

    public boolean passAccident(){
        return true;
    }

    public static List<HashMap<String, String>> getResultMap(String data) throws Exception {

        //결과값을 넣어줄 map을 선언해줍니다.
        List<HashMap<String, String>> resultMap = new LinkedList<HashMap<String, String>>();

        InputSource is = new InputSource(new StringReader(data));

        //Document 클래스로 xml데이터를 취득합니다.
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

        //xPath 팩토리로 객체를 만듭니다.
        XPath xpath = XPathFactory.newInstance().newXPath();

        //xPath를 컴파일한 후에 node단위로 데이터를 수집합니다.
        NodeList nodeList = (NodeList) xpath.compile("/result/record").evaluate(document, XPathConstants.NODESET);
        int nodeListCount = nodeList.getLength();
        for (int i = 0; i < nodeListCount; i++) {
            NodeList childNode = nodeList.item(i).getChildNodes();
            HashMap<String, String> nodeMap = new HashMap<String, String>();
            int childNodeCount = childNode.getLength();
            for (int j = 0; j < childNodeCount; j++) {
                nodeMap.put(childNode.item(j).getNodeName(), childNode.item(j).getTextContent());
            }
            resultMap.add(nodeMap);
        }
        return resultMap;
    }



}
