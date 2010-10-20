package com.pugh.sockso.music.tag.aac;

public enum AtomType {
	FTYP("ftyp")
	,MDAT("mdat")
	,MVHD("mvhd")
	,TKHD("tkhd")
	,MDHD("mdhd")
	,VMHD("vmhd")
	,SMHD("smhd")
	,HMHD("hmhd")
	,STSD("stsd")
	,STTS("stts")
	,STSZ("stsz")
	,STZ2("stz2")
	,STCO("stco")
	,STSC("stsc")
	,MP4A("mp4a")
	,MP4V("mp4v")
	,MP4S("mp4s")
	,ESDS("esds")
	,META("meta") /* iTunes Metadata box */
	,NAME("name") /* iTunes Metadata name box */
	,DATA("data") /* iTunes Metadata data box */
	,CTTS("ctts")
	,FRMA("frma")
	,IVIV("iviv")
	,PRIV("priv")
	,USER("user")
	,KEY("key ")
	,HDLR("hdlr")
	,ALBUM_ARTIST("aART")
	,CONTENTGROUP("�grp")
	,LYRICS("�lyr")
	,DESCRIPTION("desc") 
	,SORTTITLE("sonm")
	,SORTALBUM("soal")
	,SORTARTIST("soar")
	,SORTALBUMARTIST("soaa") 
	,SORTWRITER("soco")
	,SORTSHOW("sosn")
	,PODCAST("pcst") 
	,DINF("dinf")
	,UNKNOWN("----") 
	,FREE("free") 
	,SKIP("skip")
	,PGAP("pgap")
	,MOOV("moov") 
	,TRAK("trak")
	,EDTS("edts")
	,MDIA("mdia")
	,MINF("minf") 
	,STBL("stbl")
	,UDTA("udta") 
	,ILST("ilst")  
	,TITLE("�nam")
	,ARTIST("�ART")
	,WRITER("�wrt")
	,ALBUM("�alb")
	,DATE("�day")
	,TOOL("�too")
	,COMMENT("�cmt")
	,GENRE1("�gen")
	,TRACK("trkn")
	,DISC("disk")
	,COMPILATION("cpil")
	,GENRE2("gnre") 
	,TEMPO("tmpo") 
	,COVER("covr")
	,DRMS("drms")
	,SINF("sinf")
	,SCHI("schi");
		
	public static int SIZE = 4;
		
	private String name;
		
	private AtomType(String name) {
		this.name = name;
	}
		
	public static AtomType getByName(final String name) {
		for (final AtomType at : AtomType.values()) {
			if (at.name.equals(name)) {
				return at;
			}
		}
		return null;
	}
		
	public String toString() {
		return name;
	}
}
