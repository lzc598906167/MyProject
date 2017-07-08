package com.nwsuaf.plantcontrol;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 校区实体
 * 
 * @author 刘林、王俊杰
 */
public class Campus implements Parcelable {
	private String name; // 校区名
	private Coordinate coordinate; // 坐标

	public Campus() {
		coordinate = new Coordinate();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Double lat, Double log) {
		coordinate.setLat(lat);
		coordinate.setLog(log);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel write, int arg1) {
		write.writeString(name);
		write.writeDouble(coordinate.getLat());
		write.writeDouble(coordinate.getLog());
	}

	private Campus(Parcel source) {
		coordinate = new Coordinate();
		name = source.readString();
		coordinate.setLat(source.readDouble());
		coordinate.setLog(source.readDouble());
	}

	public static final Creator<Campus> CREATOR = new Creator<Campus>() {
		@Override
		public Campus createFromParcel(Parcel source) {
			return new Campus(source);
		}

		@Override
		public Campus[] newArray(int size) {
			return new Campus[size];
		}
	};
}
