package com.nwsuaf.plantcontrol;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 前端和后台交换数据的类
 * 
 * @author 刘林、王俊杰
 */
@SuppressWarnings("deprecation")
public class HttpClientUtils {
	private String responseResult = "";
	private String markBuilding = null;
	private String url;
	private String urlPlant = "http://" + StaticVal.ip + ":" + StaticVal.port
			+ "/plant/plant_getPlantByName?plantname=";

	private String urlCampus = "http://" + StaticVal.ip + ":" + StaticVal.port + "/compus/compus_sendCompusList";
	private String urlSlurDistrict = "http://" + StaticVal.ip + ":" + StaticVal.port
			+ "/section/section_getSectionsByFuzzyName?sectionname=";
	private String urlSlurPlant = "http://" + StaticVal.ip + ":" + StaticVal.port
			+ "/plant/plant_getPlantsByFuzzyName?plantname=";
	private String urlNearbyPlant = "http://" + StaticVal.ip + ":" + StaticVal.port
			+ "/plantdistri/plantdistri_getPlantsByDis?";
	private String urlAccuratePlant = "http://" + StaticVal.ip + ":" + StaticVal.port
			+ "/plant/plant_getPlantsByThreeName?";
	private String urlAccurateDistrict = "http://" + StaticVal.ip + ":" + StaticVal.port
			+ "/section/section_getPlantBySectionName?";

	public HttpClientUtils() {
	}

	/**
	 * 访问服务器获取JSON字符串
	 * 
	 * @param url
	 *            URL地址
	 * @return JSON字符串
	 */
	protected String sendHttpPost(String url) {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		HttpPost request;

		try {
			request = new HttpPost(new URI(url));
			HttpResponse response = client.execute(request);
			// 判断请求是否成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 200表示请求成功
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					String beanListToJson = EntityUtils.toString(entity, "UTF-8");
					return beanListToJson;
				}
			} else if (response.getStatusLine().getStatusCode() == 500) {
				responseResult = "error";
				return null;
			}
		} catch (Exception e) {
			responseResult = "timeout";
			return null;
		}
		return null;
	}

	/**
	 * 获取响应码
	 * 
	 * @return 响应码
	 */
	public String getResponseResult() {
		return responseResult;
	}

	/**
	 * 按植物名称搜索
	 * 
	 * @param kone
	 *            植物名
	 * @param ktwo
	 *            地区名
	 * @param kthree
	 *            校区名
	 * @return 植物搜索结果数组
	 */
	public ArrayList<Plant> getPlantByName(String kone, String ktwo, String kthree) {
		if (ktwo.equals("") && kthree.equals(""))
			url = urlPlant + kone;
		else
			url = urlAccuratePlant + "plantname=" + kone + "&sectionname=" + ktwo + "&campusname=" + kthree;
		String result = sendHttpPost(url);
		if (result != null)
			return getPlantList(result, "plant");
		return null;
	}

	/**
	 * 按地名搜索
	 * 
	 * @param kone
	 *            地区名
	 * @param ktwo
	 *            校区名
	 * @return 植物搜索结果数组
	 */
	public ArrayList<Plant> getByPlace(String kone, String ktwo) {
		url = urlAccurateDistrict + "sectionname=" + kone + "&campusname=" + ktwo;
		String result = sendHttpPost(url);
		if (result != null)
			return getPlantList(result, "place");
		return null;
	}

	/**
	 * 附近搜索
	 * 
	 * @param lat
	 *            纬度
	 * @param log
	 *            经度
	 * @return 植物搜索结果数组
	 */
	public ArrayList<Plant> getNearbyPlant(Double lat, Double log) {
		url = urlNearbyPlant + "lat=" + lat + "&log=" + log;
		String result = sendHttpPost(url);
		if (result != null) {
			return getPlantList(result, "nearby");
		}
		return null;
	}

	/**
	 * 解析JSON字符串，并且获取和设置想要的参数，得到植物列表
	 * 
	 * @param jsonString
	 *            JSON字符串
	 * @param who
	 *            搜索类型
	 * @return 植物列表
	 */
	private ArrayList<Plant> getPlantList(String jsonString, String who) {
		ArrayList<Plant> list = new ArrayList<Plant>();
		JSONObject oneJsonObject;
		try {
			oneJsonObject = new JSONObject(jsonString);

			jsonString = oneJsonObject.getString("sendPlants");
			// 最外层是一个数组[]，所以使用JSONArray
			JSONArray myJsonArray = new JSONArray(jsonString);
			for (int i = 0; i < myJsonArray.length(); i++) {
				JSONObject jsonObject = myJsonArray.getJSONObject(i);
				Plant plant = new Plant();

				// 获取到JSON对象，就可以直接通过Key获取Value
				plant.setName(jsonObject.getString("name"));
				plant.setLatName(jsonObject.getString("latName"));
				plant.setAliaseName(jsonObject.getString("aliaseName"));
				plant.setFamilyName(jsonObject.getString("familyName"));
				plant.setGenusName(jsonObject.getString("genusName"));
				plant.setDistribution(jsonObject.getString("distribution"));
				plant.setExterion(jsonObject.getString("exterion"));
				plant.setFlower(jsonObject.getString("flower"));
				plant.setBranch(jsonObject.getString("branch"));
				plant.setLeaf(jsonObject.getString("leaf"));
				plant.setFruit(jsonObject.getString("fruit"));
				String imageString = jsonObject.getString("plantimage");

				List<PlantImage> plantImages = new ArrayList<PlantImage>();
				JSONArray plantImagesJson = new JSONArray(imageString);
				for (int j = 0; j < plantImagesJson.length(); j++) {
					JSONObject twoJsonObject = plantImagesJson.getJSONObject(j);
					PlantImage plantImage = new PlantImage();
					plantImage.setType(twoJsonObject.getString("type"));
					plantImage.setPlantUrl(twoJsonObject.getString("urladdress"));
					plantImages.add(plantImage);
				}
				plant.setPlantImages(plantImages);

				String coor = jsonObject.getString("coordinateList");
				List<Coordinate> one = new ArrayList<Coordinate>();
				JSONArray coordJsonArray = new JSONArray(coor);
				for (int j = 0; j < coordJsonArray.length(); j++) {
					JSONObject twoJsonObject = coordJsonArray.getJSONObject(j);
					Double lat = twoJsonObject.getDouble("lat");
					Double log = twoJsonObject.getDouble("log");
					Coordinate twoCoordinate = new Coordinate();
					twoCoordinate.setLat(lat);
					twoCoordinate.setLog(log);
					one.add(twoCoordinate);
				}
				plant.setCoordinate(one);

				list.add(plant);
			}

			if (who.equals("place")) {
				String one = oneJsonObject.getString("sectionCoordinate");
				JSONObject two = new JSONObject(one);
				markBuilding = two.getDouble("lat") + "," + two.getDouble("log");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return list;
	}

	/**
	 * 获取校区
	 * 
	 * @return 校区数组
	 */
	public ArrayList<Campus> getTheCampus() {
		url = urlCampus;
		String result = sendHttpPost(url);
		if (result != null)
			return getCampus(result);
		return null;
	}

	/**
	 * 按地点名模糊查询
	 * 
	 * @param keyword
	 *            地点名
	 * @return 植物搜索结果条目信息数组
	 */
	public ArrayList<TabInfo> getSlurDistrcits(String keyword) {
		url = urlSlurDistrict + keyword;
		String result = sendHttpPost(url);
		if (result != null)
			return getSlurPlantDistrcits(result);
		return null;
	}

	/**
	 * 根据地区返回模糊搜索的结果
	 * 
	 * @param jsonString
	 *            JSON字符串
	 * @return 植物搜索结果条目信息数组
	 */
	private ArrayList<TabInfo> getSlurPlantDistrcits(String jsonString) {
		ArrayList<TabInfo> tabInfos = new ArrayList<TabInfo>();
		JSONObject oneJsonObject;
		try {
			oneJsonObject = new JSONObject(jsonString);
			jsonString = oneJsonObject.getString("sendSections");
			// 最外层是一个数组[]，所以使用JSONArray
			JSONArray myJsonArray = new JSONArray(jsonString);
			for (int i = 0; i < myJsonArray.length(); i++) {
				JSONObject jsonObject = myJsonArray.getJSONObject(i);
				TabInfo tabInfo = new TabInfo();
				tabInfo.setCampusName(jsonObject.getString("campusName"));
				tabInfo.setSectionName(jsonObject.getString("sectionName"));
				tabInfos.add(tabInfo);
			}
		} catch (JSONException e) {
		}
		return tabInfos;
	}

	/**
	 * 按植物名模糊查询
	 * 
	 * @param keyword
	 *            植物名
	 * @return 植物搜索结果条目信息数组
	 */
	public ArrayList<TabInfo> getSlurPlantByName(String keyword) {
		url = urlSlurPlant + keyword;
		String result = sendHttpPost(url);
		if (result != null)
			return getSlurPlantName(result);
		return null;
	}

	/**
	 * 根据植物名返回模糊搜索的结果
	 * 
	 * @param jsonString
	 *            JSON字符串
	 * @return 植物搜索结果条目信息数组
	 */
	private ArrayList<TabInfo> getSlurPlantName(String jsonString) {
		ArrayList<TabInfo> distrcits = new ArrayList<TabInfo>();
		try {
			JSONObject oneJsonObject = new JSONObject(jsonString);
			jsonString = oneJsonObject.getString("fuzzyPlants");
			JSONArray jsonarr = new JSONArray(jsonString);
			TabInfo distrcit = null;
			for (int i = 0; i < jsonarr.length(); i++) {
				distrcit = new TabInfo();
				JSONObject jsonObject = jsonarr.getJSONObject(i);
				distrcit.setCampusName(jsonObject.getString("campusName"));
				distrcit.setPlantName(jsonObject.getString("plantName"));
				distrcit.setSectionName(jsonObject.getString("sectionName"));
				distrcits.add(distrcit);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return distrcits;
	}

	/**
	 * 获取所有校区的数据
	 * 
	 * @param jsonString
	 *            JSON字符串
	 * @return 校区数组
	 */
	private ArrayList<Campus> getCampus(String jsonString) {
		ArrayList<Campus> campus = new ArrayList<Campus>();
		try {
			JSONObject oneJsonObject = new JSONObject(jsonString);
			jsonString = oneJsonObject.getString("compuses");
			JSONArray jsonarr = new JSONArray(jsonString);
			Campus campu = null;
			for (int i = 0; i < jsonarr.length(); i++) {
				campu = new Campus();
				JSONObject jsonObject = jsonarr.getJSONObject(i);
				campu.setName(jsonObject.getString("name"));
				campu.setCoordinate(jsonObject.getDouble("lat"), jsonObject.getDouble("log"));
				campus.add(campu);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return campus;
	}

	public String getMarkBuilding() {
		return markBuilding;
	}
}
