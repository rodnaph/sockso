package com.pugh.sockso.music.tag.aac;

public class Atom {
	private AtomHeader header;
	private String data;

	public Atom(AtomHeader header, byte[] data) {
		this.header = header;
		if (header.getType() == AtomType.TRACK
				|| header.getType() == AtomType.DISC) {
			long index = FileUtil.readLong(data, 10, 2);
			long total = FileUtil.readLong(data, 12, 2);
			this.data = index + ":" + total;
		} else {
			// 1 for version + 3 for flags + 4 for reserved = 8
			this.data = new String(data, 8, data.length - 8);
		}
	}

	public Atom(AtomHeader header, String data) {
		this.header = header;
		this.data = data;
	}

	public AtomHeader getHeader() {
		return header;
	}

	public String getData() {
		return data;
	}

	public String toString() {
		return "Atom: header[" + header + "] data[" + data + "]";
	}
	
	
}
