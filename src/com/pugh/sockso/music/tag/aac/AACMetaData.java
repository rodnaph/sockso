package com.pugh.sockso.music.tag.aac;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class and all related classes were created using
 * logic extracted from the faac project code.  
 * 
 * http://sourceforge.net/projects/faac/
 * 
 * @author Mark Rave mark_rave@yahoo.com
 *
 */
public class AACMetaData {
	private String track;
	private String artist;
	private String album;
	private int number;
	private int total;
	private String genre;

	public AACMetaData(File file) throws IOException {
		parse(file);
	}
	
	private static void read(FileInputStream fis, AtomHeader lastHeader,
			ArrayList<AtomHeader> headers, ArrayList<Atom> atoms, AtomType stop)
			throws IOException {
		AtomHeader ah = new AtomHeader(fis);
		if (ah.getType() == null) {
			return;
		} else {
			headers.add(ah);
		}
		if (ah.getType() == stop) {
			return;
		}
		if (ah.getType() == AtomType.META) {
			// META is special case, throw away 4 bytes of version info
			fis.skip(4);
		}
		if (lastHeader != null && ah.getType() == AtomType.DATA) {
			// DATA is special case, create Atom using the last AtomHeader
			byte[] data = new byte[(int) (ah.getSize() - AtomHeader.SIZE)];
			fis.read(data);
			atoms.add(new Atom(lastHeader, data));
		} else if (!ah.hasSubAtom()) {
			// No sub atoms so skip
			fis.skip(ah.getSize() - AtomHeader.SIZE);
		}
		read(fis, ah, headers, atoms, stop);
	}
		
	private void parse(File file) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			ArrayList<AtomHeader> headers = new ArrayList<AtomHeader>();
			ArrayList<Atom> atoms = new ArrayList<Atom>();
			try {
				read(fis, null, headers, atoms, AtomType.MDAT);
			}
			catch (IOException ioe) {
				// maybe the logic is wrong and we went to the end of file
				// by mistake, we should attempt to get what we can so 
				// search through the atoms obtained
			}
			for (Atom atom : atoms) {
				switch (atom.getHeader().getType()) {
					case TITLE:
						this.track = atom.getData();
						break;
					case ARTIST:
						this.artist = atom.getData();
						break;
					case ALBUM:
						this.album = atom.getData();
						break;
					case TRACK:
						String trackStr = atom.getData();
						if (trackStr != null && trackStr.indexOf(':') > 0) {
							try {
								this.number = Integer.parseInt(trackStr.substring(0, trackStr.indexOf(':')));
								this.total = Integer.parseInt(trackStr.substring(trackStr.indexOf(':')+1));
							}
							catch (Exception ex) {
								// Swallow any exception because either
								// value may not exist or they may not be numbers
							}
						}
						break;
					case GENRE1:
						this.genre = atom.getData();
						break;
				}
			}
		}
		finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	public String getTrack() {
		return track;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public int getNumber() {
		return number;
	}

	public int getTotal() {
		return total;
	}

	public String getGenre() {
		return genre;
	}
	
	public static void main(String[] args) throws IOException {
		AACMetaData tagger = new AACMetaData(new File(args[0]));
		System.out.println("AACMetaData: track["+tagger.getTrack()+"] artist["+tagger.getArtist()+"] album["+tagger.getAlbum()+"] number["+tagger.getNumber()+"] total["+tagger.getTotal()+"] genre["+tagger.getGenre()+"]");
	}
}
