/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: jintao05@gmail.com 
 *
 * This program is a free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation with the version of 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package cn.edu.thss.iise.beehivez.server.datamanagement.pojo;

/**
 * @author JinTao 2009.9.3
 * 
 */
public class OplogObject {
	private long event_id = -1;
	private long timestamp = -1;
	private String indexname = null;
	private String operand = null;
	private String optype = null;
	private long timecost = -1;

	// the number of place,transition,arc in the petri net added or used as a
	// query
	private int nplace = -1;
	private int ntransition = -1;
	private int narc = -1;

	// the maximum degree of the petri net added or used as a query
	private int ndegree = -1;

	// the number of models in the repository
	private long npetri = -1;

	// the number of models returned by query or changed
	private int resultsize = 1;
	public static final String ADDDATATOINDEX = "adddata";
	public static final String DELDATAFROMINDEX = "deletedata";
	public static final String QUERYDATAUSEINDEX = "query";

	/**
	 * @return the operand
	 */
	public String getOperand() {
		return operand;
	}

	/**
	 * @param operand
	 *            the operand to set
	 */
	public void setOperand(String operand) {
		this.operand = operand;
	}

	/**
	 * @return the resultsize
	 */
	public int getResultsize() {
		return resultsize;
	}

	/**
	 * @param resultsize
	 *            the resultsize to set
	 */
	public void setResultsize(int resultsize) {
		this.resultsize = resultsize;
	}

	/**
	 * @return the npetri
	 */
	public long getNpetri() {
		return npetri;
	}

	/**
	 * @param npetri
	 *            the npetri to set
	 */
	public void setNpetri(long npetri) {
		this.npetri = npetri;
	}

	/**
	 * @return the nplace
	 */
	public int getNplace() {
		return nplace;
	}

	/**
	 * @param nplace
	 *            the nplace to set
	 */
	public void setNplace(int nplace) {
		this.nplace = nplace;
	}

	/**
	 * @return the ntransition
	 */
	public int getNtransition() {
		return ntransition;
	}

	/**
	 * @param ntransition
	 *            the ntransition to set
	 */
	public void setNtransition(int ntransition) {
		this.ntransition = ntransition;
	}

	/**
	 * @return the narc
	 */
	public int getNarc() {
		return narc;
	}

	/**
	 * @param narc
	 *            the narc to set
	 */
	public void setNarc(int narc) {
		this.narc = narc;
	}

	/**
	 * @return the ndegree
	 */
	public int getNdegree() {
		return ndegree;
	}

	/**
	 * @param ndegree
	 *            the ndegree to set
	 */
	public void setNdegree(int ndegree) {
		this.ndegree = ndegree;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the event_id
	 */
	public long getEvent_id() {
		return event_id;
	}

	/**
	 * @param eventId
	 *            the event_id to set
	 */
	public void setEvent_id(long eventId) {
		event_id = eventId;
	}

	/**
	 * @return the indexname
	 */
	public String getIndexname() {
		return indexname;
	}

	/**
	 * @param indexname
	 *            the indexname to set
	 */
	public void setIndexname(String indexname) {
		this.indexname = indexname;
	}

	/**
	 * @return the optype
	 */
	public String getOptype() {
		return optype;
	}

	/**
	 * @param optype
	 *            the optype to set
	 */
	public void setOptype(String optype) {
		this.optype = optype;
	}

	/**
	 * @return the timecost
	 */
	public long getTimecost() {
		return timecost;
	}

	/**
	 * @param timecost
	 *            the timecost to set
	 */
	public void setTimecost(long timecost) {
		this.timecost = timecost;
	}

}
