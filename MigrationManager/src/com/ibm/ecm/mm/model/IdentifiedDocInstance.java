package com.ibm.ecm.mm.model;

import java.util.ArrayList;

public class IdentifiedDocInstance extends DataTableElement {
	private long id;
	private String name;
	private String extension;
	private String server;
	private String volume;
	private String path;
	private String digest;
	private ArrayList<MetadataValue> metadataValues;
	private CommencePath commencePath;
	private String snippet;
	private int snapshotDeleted;
	private Document document;
	
	public IdentifiedDocInstance() {
		setMetadataValues(new ArrayList<MetadataValue>());
		setSnippet("");
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getDigest() {
		return digest;
	}
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public String getPath() {
		return path == null ? "" : path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getNameWithoutExtension() {
		if (getExtension()!=null)
			return getName().substring(0, getName().lastIndexOf("." + getExtension()));
		return getName();
	}
	public String getVolumePath() {
		return getPath().equals("") ? getVolume() : getVolume() + "/" + getPath();
	}
	public String getFullPath() {
		return getPath().equals("") ? getVolume() + "/" + getName() : getVolume() + "/" + getPath() + "/" + getName();
	}
	public String getFullyQualifiedPath() {
		return "\\\\" + getServer() + "/" + getFullPath();
	}
	public String getUnixMountedPath() {
		return getPath().equals("") ? "/mnt/" + getVolume().toUpperCase() + "/" + getName() : "/mnt/" + getVolume().toUpperCase() + "/" + getPath() + "/" + getName();
	}

	public ArrayList<MetadataValue> getMetadataValues() {
		return metadataValues;
	}

	public void setMetadataValues(ArrayList<MetadataValue> metadataValues) {
		this.metadataValues = metadataValues;
	}
	
	public MetadataValue getMetadataValue() {
		if (getMetadataValues().size() > 0) {
			return metadataValues.get(0);			
		}
		return null;		
	}

	public CommencePath getCommencePath() {
		return commencePath;
	}

	public void setCommencePath(CommencePath commencePath) {
		this.commencePath = commencePath;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public int getSnapshotDeleted() {
		return snapshotDeleted;
	}

	public void setSnapshotDeleted(int snapshotDeleted) {
		this.snapshotDeleted = snapshotDeleted;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}


}
