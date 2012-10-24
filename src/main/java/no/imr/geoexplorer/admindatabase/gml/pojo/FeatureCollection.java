package no.imr.geoexplorer.admindatabase.gml.pojo;

import java.util.LinkedList;
import java.util.List;

public class FeatureCollection {
	private BoundedBy boundedBy;
	private List<FeatureMember> featureMember;

	public BoundedBy getBoundedBy() {
		return boundedBy;
	}
	public void setBoundedBy(BoundedBy boundedBy) {
		this.boundedBy = boundedBy;
	}
	public List<FeatureMember> getFeatureMember() {
		return featureMember;
	}
	public void setFeatureMember(List<FeatureMember> featureMember) {
		this.featureMember = featureMember;
	}
	public void addMember(FeatureMember member) {
		if (featureMember == null) {
			featureMember = new LinkedList<FeatureMember>();
		}
		featureMember.add(member);
	}
}
