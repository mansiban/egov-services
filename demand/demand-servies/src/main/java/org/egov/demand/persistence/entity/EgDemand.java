/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.demand.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * EgDemand entity.
 *
 * @author Ramki
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "eg_demand")
@SequenceGenerator(name = EgDemand.SEQ_EGDEMAND, sequenceName = EgDemand.SEQ_EGDEMAND, allocationSize = 1)
public class EgDemand implements java.io.Serializable {

	private static final long serialVersionUID = 5516235021474507123L;
	public static final String SEQ_EGDEMAND = "SEQ_EG_DEMAND";
	@Id
	@GeneratedValue(generator = SEQ_EGDEMAND, strategy = GenerationType.SEQUENCE)
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_installment")
	private Installment egInstallmentMaster;
	@Column(name = "base_demand")
	private BigDecimal baseDemand = BigDecimal.ZERO;
	@Column(name = "is_history")
	private String isHistory;
	@Column(name = "create_date")
	private Date createDate;
	@Column(name = "modified_date")
	private Date modifiedDate;
	@OneToMany(mappedBy = "egDemand", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<EgDemandDetails> egDemandDetails = new HashSet<>(0);
	@OneToMany(mappedBy = "egDemand", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<EgBill> egBills = new HashSet<>(0);
	@Column(name = "amt_collected")
	private BigDecimal amtCollected = BigDecimal.ZERO;
	@Column(name = "status")
	private Character status;
	@Column(name = "min_amt_payable")
	private BigDecimal minAmtPayable = BigDecimal.ZERO;
	@Column(name = "amt_rebate")
	private BigDecimal amtRebate = BigDecimal.ZERO;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		EgDemand other = (EgDemand) obj;
		if (id != null && other != null && id.equals(other.id)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns a copy that can be associated with another billing system entity.
	 * The copy has the same amounts, installment, time stamps and (cloned)
	 * demand-details if any. It will NOT have a copy of the EgBills of the
	 * original demand. (Note: making it public instead of protected to allow
	 * any class to use it.)
	 */
	@Override
	public Object clone() {
		EgDemand clone = null;
		try {
			clone = (EgDemand) super.clone();
		} catch (CloneNotSupportedException e) {
			// this should never happen
			throw new InternalError(e.toString());
		}
		clone.setId(null);
		clone.setEgBills(new HashSet<EgBill>());
		clone.setEgDemandDetails(new HashSet<EgDemandDetails>());
		for (EgDemandDetails det : egDemandDetails) {
			clone.addEgDemandDetails((EgDemandDetails) det.clone());
		}
		return clone;
	}

	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public BigDecimal getMinAmtPayable() {
		return minAmtPayable;
	}

	public void setMinAmtPayable(BigDecimal minAmtPayable) {
		this.minAmtPayable = minAmtPayable;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Installment getEgInstallmentMaster() {
		return this.egInstallmentMaster;
	}

	public void setEgInstallmentMaster(Installment egInstallmentMaster) {
		this.egInstallmentMaster = egInstallmentMaster;
	}

	public BigDecimal getBaseDemand() {
		return this.baseDemand;
	}

	public void setBaseDemand(BigDecimal baseDemand) {
		this.baseDemand = baseDemand;
	}

	public void addBaseDemand(BigDecimal amount) {
		setBaseDemand(getBaseDemand().add(amount));
	}

	public String getIsHistory() {
		return this.isHistory;
	}

	public void setIsHistory(String isHistory) {
		this.isHistory = isHistory;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Set<EgBill> getEgBills() {
		return this.egBills;
	}

	public void setEgBills(Set<EgBill> egBills) {
		this.egBills = egBills;
	}

	public Set<EgDemandDetails> getEgDemandDetails() {
		return egDemandDetails;
	}

	public void setEgDemandDetails(Set<EgDemandDetails> egDemandDetails) {
		this.egDemandDetails = egDemandDetails;
	}

	public void addEgBill(EgBill egBill) {
		getEgBills().add(egBill);
	}

	public void removeEgBill(EgBill egBill) {
		getEgBills().remove(egBill);
	}

	public void addEgDemandDetails(EgDemandDetails egDemandDetails) {
		getEgDemandDetails().add(egDemandDetails);
	}

	public void removeEgDemandDetails(EgDemandDetails egDemandDetails) {
		getEgDemandDetails().remove(egDemandDetails);
	}

	public BigDecimal getAmtCollected() {
		return amtCollected;
	}

	public void setAmtCollected(BigDecimal amtCollected) {
		this.amtCollected = amtCollected;
	}

	/**
	 * Adds an amount to the existing collected amount.
	 */
	public void addCollected(BigDecimal amount) {
		if (getAmtCollected() != null) {
			setAmtCollected(getAmtCollected().add(amount != null ? amount : BigDecimal.ZERO));
		} else {
			setAmtCollected(amount);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append("|").append(egInstallmentMaster).append("|").append(baseDemand).append("|")
				.append(amtCollected).append("|").append(egDemandDetails).append("|").append(amtRebate);
		return sb.toString();
	}

	public BigDecimal getAmtRebate() {
		return amtRebate;
	}

	public void setAmtRebate(BigDecimal amtRebate) {
		this.amtRebate = amtRebate;
	}

	public void addRebateAmt(BigDecimal rebateAmt) {
		if (getAmtRebate() != null) {
			setAmtRebate(getAmtRebate().add(rebateAmt != null ? rebateAmt : BigDecimal.ZERO));
		} else {
			setAmtRebate(rebateAmt);
		}
	}
}