package com.nwsuaf.plantcontrol;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 坐标描述
 * 
 * @author 刘林、王俊杰
 */
public class Coordinate implements Parcelable {
	private Double lat; // 纬度
	private Double log; // 经度

	public Coordinate(String oneString) {
		String[] twoString = oneString.split(",");
		this.lat = Double.parseDouble(twoString[0]);
		this.log = Double.parseDouble(twoString[1]);
	}

	public Coordinate(Coordinate p) {
		super();
		this.lat = p.lat;
		this.log = p.log;
	}

	public Coordinate() {
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLog() {
		return log;
	}

	public void setLog(Double log) {
		this.log = log;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel write, int arg1) {
		write.writeDouble(lat);
		write.writeDouble(log);
	}

	private Coordinate(Parcel source) {
		lat = source.readDouble();
		log = source.readDouble();
	}

	public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
		@Override
		public Coordinate createFromParcel(Parcel source) {
			return new Coordinate(source);
		}

		@Override
		public Coordinate[] newArray(int size) {
			return new Coordinate[size];
		}
	};
}
