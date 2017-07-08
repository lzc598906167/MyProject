package com.nwsuaf.plantcontrol;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * ֲ�����������Ŀ��Ϣ
 * 
 * @author ���֡�������
 */
public class TabInfo implements Parcelable {
	private String campusName; // У����
	private String plantName; // ֲ����
	private String sectionName; // ������

	public String getCampusName() {
		return campusName;
	}

	public void setCampusName(String campusName) {
		this.campusName = campusName;
	}

	public String getPlantName() {
		return plantName;
	}

	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public TabInfo() {
	}

	private TabInfo(Parcel source) {
		campusName = source.readString();
		plantName = source.readString();
		sectionName = source.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel write, int arg1) {
		write.writeString(campusName);
		write.writeString(plantName);
		write.writeString(sectionName);
	}

	public static final Creator<TabInfo> CREATOR = new Creator<TabInfo>() {
		@Override
		public TabInfo createFromParcel(Parcel source) {
			return new TabInfo(source);
		}

		@Override
		public TabInfo[] newArray(int size) {
			return new TabInfo[size];
		}
	};
}
