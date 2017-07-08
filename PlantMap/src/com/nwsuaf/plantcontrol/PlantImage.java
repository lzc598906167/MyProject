package com.nwsuaf.plantcontrol;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ֲ��ͼƬ����
 * 
 * @author ���֡�������
 */
public class PlantImage implements Parcelable {
	private String type; // ͼƬ���ͣ�ֲ�ﲿλ��
	private String plantUrl; // URL��ַ

	public PlantImage() {
	}

	public PlantImage(Parcel source) {
		type = source.readString();
		plantUrl = source.readString();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPlantUrl() {
		return plantUrl;
	}

	public void setPlantUrl(String plantUrl) {
		this.plantUrl = plantUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel source, int arg1) {
		source.writeString(type);
		source.writeString(plantUrl);
	}

	public static final Creator<PlantImage> CREATOR = new Creator<PlantImage>() {
		@Override
		public PlantImage createFromParcel(Parcel source) {
			return new PlantImage(source);
		}

		@Override
		public PlantImage[] newArray(int size) {
			return new PlantImage[size];
		}
	};
}
