package br.org.rpf.cagef.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author elbio.caetano
 */
@Entity
@Table(name = "casasoracao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class PrayingHouse implements Serializable {
	private static final long serialVersionUID = 5359458823838538383L;

	@Id
	@Column(name = "cod_relatorio", unique = true)
	private String reportCode;
	@OneToOne
	@JoinColumn(name = "id_cidade")
	private City city;
	@Column(name = "bairro")
	private String district;
	@JsonIgnore
	@Column(name = "updated_at")
	private Date updatedAt;
	@JsonIgnore
	@Column(name = "created_at")
	private Date createdAt;
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.MERGE, mappedBy = "ministryOrPosition")
	@Builder.Default
	private Collection<Volunteer> volunteers = new ArrayList<>();

	public PrayingHouse(String reportCode, String district, City city) {
		this.reportCode = reportCode;
		this.district = district;
		this.city = city;
	}

	public PrayingHouse(String district, City city) {
		this.district = district;
		this.city = city;
	}

	public void addVolunteer(Volunteer volunteer) {
		if (volunteers.contains(volunteer))
			return;
		volunteers.add(volunteer);
		volunteer.setPrayingHouse(this);
	}

	public void removeVolunteer(Volunteer volunteer) {
		if (!volunteers.contains(volunteer))
			return;
		volunteers.remove(volunteer);
		volunteer.setPrayingHouse(null);
	}

}
