package com.pugh.sockso.music.tag.aac;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class AtomHeader {
	public static int SIZE = 8;

	long size;
	AtomType type;

	private static Set<AtomType> haveSubAtomsList = new TreeSet<AtomType>(
			Arrays.asList(new AtomType[] { AtomType.MOOV, AtomType.TRAK,
							AtomType.MDIA, AtomType.MINF, AtomType.STBL,
							AtomType.UDTA, AtomType.ILST, AtomType.TITLE,
							AtomType.ARTIST, AtomType.WRITER, AtomType.ALBUM,
							AtomType.DATE, AtomType.TOOL, AtomType.COMMENT,
							AtomType.GENRE1, AtomType.TRACK, AtomType.DISC,
							AtomType.COMPILATION, AtomType.GENRE2,
							AtomType.TEMPO, AtomType.COVER, AtomType.DRMS,
							AtomType.SINF, AtomType.SCHI, AtomType.META }));

	public AtomHeader(long size, AtomType type) {
		this.size = size;
		this.type = type;
	}

	public AtomHeader(FileInputStream fis) throws IOException {
		this.size = FileUtil.readLong(fis, 4);
		this.type = AtomType.getByName(readAtomType(fis));
	}

	private static String readAtomType(FileInputStream fis) throws IOException {
		byte[] bytes = new byte[AtomType.SIZE];
		fis.read(bytes);
		return new String(bytes);
	}
	
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public AtomType getType() {
		return type;
	}

	public void setType(AtomType type) {
		this.type = type;
	}

	public boolean hasSubAtom() {
		return haveSubAtomsList.contains(type);
	}
	
	public String toString() {
		return "AtomHeader size[" + size + "] type[" + type + "]";
	}
}
