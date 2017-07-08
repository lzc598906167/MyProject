package com.nwsuaf.plantcontrol;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * ֲ��Bean
 * 
 * @author ���֡�������
 */
public class Plant implements Parcelable {
	private String name; // ѧ��
	private String latName; // ����ѧ��
	private String aliaseName; // ����
	private String familyName; // ��
	private String genusName; // ����
	private String distribution; // �ֲ�
	private String exterion; // ���
	private String flower; // ������
	private String branch; // ֦��
	private String leaf; // ҶƬ
	private String fruit; // ��ʵ
	private List<Coordinate> coordinateList = new ArrayList<Coordinate>(); // γ��
	private List<PlantImage> plantImages = new ArrayList<PlantImage>(); // ͼƬ����

	public List<PlantImage> getPlantImages() {
		return plantImages;
	}

	public void setPlantImages(List<PlantImage> plantImages) {
		this.plantImages = plantImages;
	}

	public Plant(Plant p) {
		super();
		this.name = p.name;
		this.latName = p.latName;
		this.aliaseName = p.aliaseName;
		this.familyName = p.familyName;
		this.genusName = p.genusName;
		this.distribution = p.distribution;
		this.exterion = p.exterion;
		this.flower = p.flower;
		this.branch = p.branch;
		this.leaf = p.leaf;
		this.fruit = p.fruit;
		coordinateList.clear();
		for (Coordinate c : p.coordinateList) {
			Coordinate qCoordinate = new Coordinate(c);
			coordinateList.add(qCoordinate);
		}
		this.plantImages = p.plantImages;
	}

	public Plant() {
	}

	/**
	 * ���ݾ������ͬһ��ֲ��ĸ���
	 * 
	 * @param one
	 *            ����
	 */
	public void clearPartCoodinate(float one) {
		if (coordinateList.size() == 0)
			return;
		ArrayList<Coordinate> conformity = new ArrayList<Coordinate>(); // ����Ҫ�������
		ArrayList<Coordinate> inconformity = new ArrayList<Coordinate>(); // ������Ҫ�������
		Coordinate oneCoordinate = coordinateList.get(0);
		conformity.add(oneCoordinate);
		Boolean isfirst = true;
		while (coordinateList.size() > 1) {
			isfirst = false;
			for (int i = 1; i < coordinateList.size(); i++) {
				Coordinate twoCoordinate = coordinateList.get(i);
				if (myCompare(oneCoordinate, twoCoordinate) < one)
					inconformity.add(twoCoordinate);
			}
			for (Coordinate c : inconformity)
				coordinateList.remove(c);
			coordinateList.remove(0);
			inconformity.clear();
			if (coordinateList.size() > 0) {
				oneCoordinate = coordinateList.get(0);
				conformity.add(oneCoordinate);
			} else
				break;
		}

		if (coordinateList.size() == 1 && isfirst)
			conformity.add(coordinateList.get(0));
		coordinateList.clear();
		for (Coordinate c : conformity)
			coordinateList.add(c);
	}

	/**
	 * 2�������֮��ľ�������
	 * 
	 * @param start
	 *            ��ʼ�����
	 * @param end
	 *            ���������
	 * @return ����
	 */
	private float myCompare(Coordinate start, Coordinate end) {
		float[] results = { 0 };
		results[0] = (float) 10000.1;

		if (start != null && end != null)
			Location.distanceBetween(start.getLat(), start.getLog(), end.getLat(), end.getLog(), results);
		return results[0];
	}

	public List<Coordinate> getCoordinate() {
		return coordinateList;
	}

	public void setCoordinate(List<Coordinate> coordinates) {
		this.coordinateList = coordinates;
	}

	public Plant(String chineseName, double latitude, double longitude) {
		super();
		this.name = chineseName;
		Coordinate oneCoordinate = new Coordinate();
		oneCoordinate.setLat(latitude);
		oneCoordinate.setLog(longitude);
		coordinateList.add(oneCoordinate);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLatName() {
		return latName;
	}

	public void setLatName(String latName) {
		this.latName = latName;
	}

	public String getAliaseName() {
		return aliaseName;
	}

	public void setAliaseName(String aliaseName) {
		this.aliaseName = aliaseName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getGenusName() {
		return genusName;
	}

	public void setGenusName(String genusName) {
		this.genusName = genusName;
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}

	public String getExterion() {
		return exterion;
	}

	public void setExterion(String exterion) {
		this.exterion = exterion;
	}

	public String getFlower() {
		return flower;
	}

	public void setFlower(String flower) {
		this.flower = flower;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getLeaf() {
		return leaf;
	}

	public void setLeaf(String leaf) {
		this.leaf = leaf;
	}

	public String getFruit() {
		return fruit;
	}

	public void setFruit(String fruit) {
		this.fruit = fruit;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel write, int arg1) {
		write.writeString(name);
		write.writeString(latName);
		write.writeString(aliaseName);
		write.writeString(familyName);
		write.writeString(genusName);
		write.writeString(distribution);
		write.writeString(exterion);
		write.writeString(flower);
		write.writeString(branch);
		write.writeString(leaf);
		write.writeString(fruit);
		write.writeList(coordinateList);
		write.writeList(plantImages);
	}

	@SuppressWarnings("unchecked")
	private Plant(Parcel source) {
		name = source.readString();
		latName = source.readString();
		aliaseName = source.readString();
		familyName = source.readString();
		genusName = source.readString();
		distribution = source.readString();
		exterion = source.readString();
		flower = source.readString();
		branch = source.readString();
		leaf = source.readString();
		fruit = source.readString();
		coordinateList = source.readArrayList(Coordinate.class.getClassLoader());
		plantImages = source.readArrayList(PlantImage.class.getClassLoader());
	}

	public static final Creator<Plant> CREATOR = new Creator<Plant>() {
		@Override
		public Plant createFromParcel(Parcel arg0) {
			return new Plant(arg0);
		}

		@Override
		public Plant[] newArray(int size) {
			return new Plant[size];
		}
	};
}
