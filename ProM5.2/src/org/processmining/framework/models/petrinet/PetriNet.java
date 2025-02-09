/***********************************************************
 *      This software is part of the ProM package          *
 *             http://www.processmining.org/               *
 *                                                         *
 *            Copyright (c) 2003-2006 TU/e Eindhoven       *
 *                and is licensed under the                *
 *            Common Public License, Version 1.0           *
 *        by Eindhoven University of Technology            *
 *           Department of Information Systems             *
 *                 http://is.tm.tue.nl                     *
 *                                                         *
 **********************************************************/

package org.processmining.framework.models.petrinet;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;

import org.processmining.framework.log.LogEvent;
import org.processmining.framework.models.ModelGraph;
import org.processmining.framework.models.ModelGraphEdge;
import org.processmining.framework.models.ModelGraphPanel;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.ui.Message;
import org.processmining.framework.util.Dot;

import cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation.ResourcePetriNet;
import cn.edu.thss.iise.beehivez.client.ui.modelpetriresourceallocation.ResourceTransition;

import att.grappa.Attribute;
import att.grappa.Edge;
import att.grappa.Graph;
import att.grappa.GrappaAdapter;
import att.grappa.Node;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * A Petri net is a data structure that is used to model concurrent behavior. It
 * can be represented as a bipartite (that is, the two types of node can only be
 * connected in an alternating manner), directed graph connecting
 * <code>Transition</code> nodes with <code>Place</code> nodes.
 * 
 * @see Transition
 * @see Place
 * @see PNEdge
 * @see Token
 * 
 * @author Boudewijn van Dongen
 */
public class PetriNet extends ModelGraph implements Cloneable {

	private ArrayList<TransitionCluster> transitionClusters;

	/** a list of groups of transitions */
	private ArrayList<Transition> transitions; /* the list of transition nodes */

	private ArrayList<Place> places; /* the list of place nodes */

	private boolean hideSimpleClusters = true;

	private int caseid;

	/**
	 * Creates an empty Petri net. That is, not containing any transitions or
	 * places.
	 */
	public PetriNet() {
		super("Petri net");
		transitionClusters = new ArrayList<TransitionCluster>();
		transitions = new ArrayList<Transition>();
		places = new ArrayList<Place>();
		/*
		 * September 5, 2005, Eric Verbeek Allow for weighted arcs (multiple
		 * edges).
		 */
		setWeightedArcs(true);
	}

	/**
	 * Adds a given cluster of transitions to the Petri net. This cluster should
	 * contain a set of trantision objects that also appear in this Petri net.
	 * The cluster is used when writing to DOT to group these transitions. Note
	 * that transitions should only occur in one transition cluster, but this is
	 * not checked nor enforced.
	 * 
	 * @param tc
	 *            the TransitionCluster to be added
	 */
	public void addCluster(TransitionCluster tc) {
		if (!transitionClusters.contains(tc)) {
			transitionClusters.add(tc);
		}
	}

	public void setHideSimpleClusters(boolean b) {
		hideSimpleClusters = b;
	}

	/**
	 * Returns all <code>TransitionCluster</code> objects known to this
	 * PetriNet.
	 * 
	 * @see TransitionCluster
	 * 
	 * @return a list containing all transition clusters
	 */
	public ArrayList<TransitionCluster> getClusters() {
		return transitionClusters;
	}

	/**
	 * Gets the <code>Transition</code> nodes contained in this Petri net.
	 * 
	 * @return a list of all transitions contained
	 */
	public ArrayList<Transition> getTransitions() {
		return transitions;
	}

	/**
	 * Gets the <code>Place</code> nodes contained in this Petri net.
	 * 
	 * @return a list of all places contained
	 */
	public ArrayList<Place> getPlaces() {
		return places;
	}

	public ArrayList<PNNode> getNodes() {
		ArrayList<PNNode> nodes = new ArrayList<PNNode>(places);
		nodes.addAll(transitions);
		return nodes;
	}

	public Set<PNNode> getNodeSet() {
		Set<PNNode> nodes = new HashSet<PNNode>(places);
		nodes.addAll(transitions);
		return nodes;
	}

	/**
	 * Merges two given places into one single place. That is, all predecessor
	 * transitions from both places will be connected via an edge to the new
	 * place as a source node, while all successor transitions will be connected
	 * to the new place as a target node. The two old places and all edges
	 * attached to them will be removed.
	 * 
	 * @param p1
	 *            the first place to be merged
	 * @param p2
	 *            the second place to be merged
	 * @return the merged place, if both given places exist in this Petri net.
	 *         <code>Null</code> otherwise.
	 */
	public Place mergePlaces(Place p1, Place p2) {
		if (!places.contains(p1) || !places.contains(p2)) {
			return null;
		}
		Iterator it = p2.getPredecessors().iterator();
		while (it.hasNext()) {
			Transition t = (Transition) it.next();
			addEdge(t, p1);
		}
		it = p2.getSuccessors().iterator();
		while (it.hasNext()) {
			Transition t = (Transition) it.next();
			addEdge(p1, t);
		}
		delPlace(p2);
		return p2;
	}

	/**
	 * Adds a given transition to this Petri net. <br>
	 * Note that the transition is expected to be associated to this Petri net
	 * already (so t.getSubgraph() should return this net). If you need that
	 * link to be established use {@link addAndLinkTransition(Transition t)
	 * addAndLinkTransition(Transition t)} instead.
	 * 
	 * @param t
	 *            the transition to be added
	 * @return the newly added transition
	 */
	public Transition addTransition(Transition t) {
		addVertex(t);
		transitions.add(t);
		return t;
	}

	/**
	 * Adds a given Transition to this Petri net. <br>
	 * Note that the transition is expected not to be associated to this Petri
	 * net already and this link will be established. If that link has been
	 * established already use {@link addTransition(Transition t)
	 * addTransition(Transition t)} instead.
	 * 
	 * @param t
	 *            the transition to be added
	 * @return the newly added transition
	 */
	public Transition addAndLinkTransition(Transition t) {
		// note that setSubgraph already calls the subgraph.addNode() method
		// for this net - so calling addVertex(t) as in addTransition would
		// result in adding the transition node to the graph twice!

		/**
		 * @todo anne: however, t will not be added to vertices list of
		 *       ModelGraph class! remove this hack as soon as resolved
		 */
		vertices.add(t);
		t.setSubgraph(this);
		transitions.add(t);
		return t;
	}

	/**
	 * @deprecated Please now use addTransition(Transition t);
	 * @see addTranstition(Transition t)
	 * 
	 *      Adds a new transition to this Petri net. The transition is backed up
	 *      by a LogEvent
	 * 
	 * @param e
	 *            the LogEvent of the transition to be added
	 * @return the newly added transition
	 */
	public Transition addTransition(LogEvent e) {
		return addTransition(new Transition(e, this));
	}

	/**
	 * Deletes a given transition to this Petri net. Note that all edges
	 * attached to this node will be removed as well.
	 * 
	 * @param t
	 *            the transition to be removed
	 */
	public void delTransition(Transition t) {
		transitions.remove(t);
		removeVertex(t);
	}

	/**
	 * Deletes a given place from this Petri net. Note that all edges attached
	 * to this node will be removed as well.
	 * 
	 * @param p
	 *            the place to be removed
	 */
	public void delPlace(Place p) {
		places.remove(p);
		removeVertex(p);
	}

	/**
	 * Adds a given place to this Petri net. <br>
	 * Note that the place is expected to be associated to this Petri net
	 * already (so t.getSubgraph() should return this net). If you need that
	 * link to be established use {@link addAndLinkPlace(Place p)
	 * addAndLinkPlace(Place p)} instead.
	 * 
	 * @param p
	 *            the place to be added
	 * @return the newly added place
	 */
	public Place addPlace(Place p) {
		/**
		 * @todo Review Anne: As we assume the identifier of a place to be
		 *       unique in a Petri net, we should check for another place
		 *       already having this identifier here, too (and throw an
		 *       exception).
		 */
		addVertex(p);
		places.add(p);
		return p;
	}

	/**
	 * Adds a given place to this Petri net. <br>
	 * Note that the place is expected not to be associated to this Petri net
	 * already and this link will be established. If that link has been
	 * established already use {@link addPlace(Place p) addPlace(Place p)}
	 * instead.
	 * 
	 * @param p
	 *            the place to be added
	 * @return the newly added place
	 */
	public Place addAndLinkPlace(Place p) {
		/**
		 * @todo Review Anne: As we assume the identifier of a place to be
		 *       unique in a Petri net, we should check for another place
		 *       already having this identifier here, too (and throw an
		 *       exception).
		 */

		// note that setSubgraph already calls the subgraph.addNode() method
		// for this net - so calling addVertex(p) as in addPlace would
		// result in adding the place node to the graph twice!
		/**
		 * @todo anne: however, p will not be added to vertices list of
		 *       ModelGraph class! remove this hack as soon as resolved
		 */
		vertices.add(p);
		p.setSubgraph(this);
		places.add(p);
		return p;
	}

	/**
	 * Adds a new place with a given identifier to this Petri net. In the case
	 * there is already a place with that identifier in this net, no new place
	 * will be created but the existing place will be returned.
	 * 
	 * @param identifier
	 *            the string specifying the identifier
	 * @return the newly created place, if the identifier was not assigned yet.
	 *         The existing place otherwise.
	 */
	public Place addPlace(String identifier) {
		Place p = findPlace(identifier);
		if (p == null) {
			p = new Place(identifier, this);
			addVertex(p);
			places.add(p);
		}
		return p;
	}

	/**
	 * Adds ...
	 * 
	 * @todo: Provide documentation
	 * 
	 * @param pn
	 *            the Petri net to be added
	 */
	public void addNet(PetriNet pn) {

		// Copy non existing transitions:
		Iterator it = pn.getTransitions().iterator();
		while (it.hasNext()) {
			Transition t = (Transition) it.next();
			if (findTransition(t) == null) {
				addAndLinkTransition(new Transition(t));
			}
		}

		// Copy non existing places:
		it = pn.getPlaces().iterator();
		while (it.hasNext()) {
			Place p;
			p = (Place) it.next();
			// See if this contains a place with the same set
			// of incoming and outgoing transitions:

			Set in = new HashSet();
			Iterator it1 = p.getPredecessors().iterator();
			while (it1.hasNext()) {
				in.add(findTransition((Transition) it1.next()));
			}

			Set out = new HashSet();
			it1 = p.getSuccessors().iterator();
			while (it1.hasNext()) {
				out.add(findTransition((Transition) it1.next()));
			}

			Place p2 = findPlace(in, out);
			if (p2 == null) {
				p2 = addPlace("");
				it1 = in.iterator();
				while (it1.hasNext()) {
					addEdge((Transition) it1.next(), p2);
				}
				it1 = out.iterator();
				while (it1.hasNext()) {
					addEdge(p2, (Transition) it1.next());
				}
			}
		}
	}

	/*
	 * September 5, 2005 Both addEdge methods removed, and both addEdgeQuick
	 * methods renamed to addEdge. A PetriNet now allows for multiple arcs, so
	 * there is no need to test this any more.
	 */

	/**
	 * Adds a new PNEdge from the given place (source node) to the given
	 * transition (target node).
	 * 
	 * @param p
	 *            the place specified as the source node for the new edge
	 * @param t
	 *            the transition specified as the target node for the new edge
	 * @return <code>true</code> if both the transition and the place are
	 *         contained in this Petri net, <code>false</code> otherwise
	 */
	public boolean addEdge(Place p, Transition t) {
		if (!places.contains(p)) {
			return false;
		}
		if (!transitions.contains(t)) {
			return false;
		}
		addEdge(new PNEdge(p, t));
		return true;
	}

	/**
	 * Adds a given edge from the given place (source node) to the given
	 * transition (target node). Note that head and tail node of the edge will
	 * be updated correspondignly.
	 * 
	 * @param e
	 *            the edge to be added from p to t
	 * @param p
	 *            the place specified as the source node for the new edge
	 * @param t
	 *            the transition specified as the target node for the new edge
	 * @return <code>true</code> if both the transition and the place are
	 *         contained in this Petri net, <code>false</code> otherwise
	 */
	public boolean addAndLinkEdge(PNEdge e, Place p, Transition t) {
		if (!places.contains(p)) {
			return false;
		}
		if (!transitions.contains(t)) {
			return false;
		}
		e.setSourceAndTargetNode(p, t);
		addEdge(e);
		return true;
	}

	/**
	 * Adds an edge from the given transition (source node) to the given place
	 * (target node).
	 * 
	 * @param t
	 *            the transition specified as the source node for the new edge
	 * @param p
	 *            the place specified as the target node for the new edge
	 * @return <code>true</code> if both the transition and the place are
	 *         contained in this Petri net, <code>false</code> otherwise
	 */
	public boolean addEdge(Transition t, Place p) {
		if (!places.contains(p)) {
			return false;
		}
		if (!transitions.contains(t)) {
			return false;
		}
		addEdge(new PNEdge(t, p));
		return true;
	}

	/**
	 * Adds an edge from the given transition (source node) to the given place
	 * (target node).
	 * 
	 * @param e
	 *            the edge to be added from t to p
	 * @param t
	 *            the transition specified as the source node for the new edge
	 * @param p
	 *            the place specified as the target node for the new edge
	 * @return <code>true</code> if both the transition and the place are
	 *         contained in this Petri net, <code>false</code> otherwise
	 */
	public boolean addAndLinkEdge(PNEdge e, Transition t, Place p) {
		if (!places.contains(p)) {
			return false;
		}
		if (!transitions.contains(t)) {
			return false;
		}
		e.setSourceAndTargetNode(t, p);
		addEdge(e);
		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * When a petri net is asked for its visualization, a temporary dot file is
	 * written and afterwards read by grappa to convert it into a java frame.
	 * Therefore, to modify the vizualization of a Petri net, this method needs
	 * to be overridden.
	 * <p>
	 * Since internally the write procedure is decomposed into semantically
	 * distinct sections, it is recommended to only override the sub procedures
	 * that are affected by the given modifications.
	 * <p>
	 * The partial steps include: initDotWriting, writeTransitionsToDot,
	 * writePlacesToDot, writeEdgesToDot, and writeAdditionsToDot.
	 * 
	 * @param bw
	 *            the writer used by the framework to create the temporary dot
	 *            file
	 * @throws IOException
	 *             if writing to the writer fails
	 */
	public void writeToDot(Writer bw) throws IOException {
		initDotWriting(bw);
		writeTransitionsToDot(bw);
		writePlacesToDot(bw);
		writeEdgesToDot(bw);
		writeClustersToDot(bw);
		finishDotWriting(bw);
	}

	/**
	 * This is the part of the {@link #writeToDot writeToDot} procedure which
	 * initializes the temporary dot file by setting font, size, margin, etc.
	 * parameters.
	 * 
	 * @param bw
	 *            the writer used by the framework to create the temporary dot
	 *            file
	 * @throws IOException
	 *             if writing to the writer fails
	 */
	protected void initDotWriting(Writer bw) throws IOException {
		// write general settings to the dot file
		bw
				.write("digraph G {ranksep=\".3\"; fontsize=\"8\"; remincross=true; margin=\"0.0,0.0\"; ");
		bw.write("fontname=\"Arial\";rankdir=\"LR\"; \n");
		bw.write("edge [arrowsize=\"0.5\"];\n");
		bw
				.write("node [height=\".2\",width=\".2\",fontname=\"Arial\",fontsize=\"8\"];\n");
		// used to connect Petri net nodes to later grappa components
		nodeMapping.clear();
		// assign unique number to every transition
		// (needed when writing the edges as the identifier might be identical
		// for two transitions)
		Iterator it = getTransitions().iterator();
		int i = 0;
		while (it.hasNext()) {
			Transition t = (Transition) (it.next());
			t.setNumber(i);
			i++;
		}
		// assign unique number to every place
		it = getPlaces().iterator();
		i = 0;
		while (it.hasNext()) {
			Place p = (Place) (it.next());
			p.setNumber(i);
			i++;
		}
	}

	/**
	 * This is the transition writing part of the {@link #writeToDot writeToDot}
	 * procedure.
	 * 
	 * @param bw
	 *            the writer used by the framework to create the temporary dot
	 *            file
	 * @throws IOException
	 *             if writing to the writer fails
	 */
	protected void writeTransitionsToDot(Writer bw) throws IOException {
		Iterator it = getTransitions().iterator();
		while (it.hasNext()) {
			Transition t = (Transition) (it.next());
			String label = t.getIdentifier();
			// write to dot
			bw.write("t"
					+ t.getNumber()
					+ " [shape=\"box\""
					+ (t.getLogEvent() != null ? ",label=\"" + label + "\""
							: ",label=\"\",style=\"filled\"") + "];\n");
			// connect Petri net nodes to later grappa components
			nodeMapping.put(new String("t" + t.getNumber()), t);
		}
	}

	/**
	 * This is the place writing part of the {@link #writeToDot writeToDot}
	 * procedure.
	 * 
	 * @param bw
	 *            the writer used by the framework to create the temporary dot
	 *            file
	 * @throws IOException
	 *             if writing to the writer fails
	 */
	protected void writePlacesToDot(Writer bw) throws IOException {
		Iterator it = this.getPlaces().iterator();
		while (it.hasNext()) {
			Place p = (Place) (it.next());
			bw.write("p"
					+ p.getNumber()
					+ " [shape=\"circle\",label=\""
					+
					/**
					 * @todo Review Anne: if place labels shall not be written,
					 *       the following line can be removed
					 */
					// (p.getIdentifier() == null ? "" :
					// p.getIdentifier().replace('"', '\'')) +
					(p.getNumberOfTokens() == 0 ? "" : "t:"
							+ p.getNumberOfTokens()) + "\"];\n");

			// connect Petri net nodes to later grappa components
			nodeMapping.put(new String("p" + p.getNumber()), p);
		}
	}

	/**
	 * This is the edge writing part of the {@link #writeToDot writeToDot}
	 * procedure.
	 * 
	 * @param bw
	 *            the writer used by the framework to create the temporary dot
	 *            file
	 * @throws IOException
	 *             if writing to the writer fails
	 */
	protected void writeEdgesToDot(Writer bw) throws IOException {
		Iterator it = this.getEdges().iterator();
		while (it.hasNext()) {
			PNEdge e = (PNEdge) (it.next());
			if (e.isPT()) {
				Place p = (Place) e.getSource();
				Transition t = (Transition) e.getDest();
				bw.write("p" + p.getNumber() + " -> t" + t.getNumber());
			} else {
				Place p = (Place) e.getDest();
				Transition t = (Transition) e.getSource();
				bw.write("t" + t.getNumber() + " -> p" + p.getNumber());
			}
			bw.write("[label=\"\"];\n");
		}
	}

	/**
	 * This is the cluster writing part of the {@link #writeToDot writeToDot}
	 * procedure. The transition clusters are visualized drawing a blue box
	 * around those transitions belonging to one group, i.e., cluster.
	 * 
	 * @see getClusters
	 * @see makeClusters
	 * 
	 * @param bw
	 *            the writer used by the framework to create the temporary dot
	 *            file
	 * @throws IOException
	 *             if writing to the writer fails
	 */
	protected void writeClustersToDot(Writer bw) throws IOException {
		int i = 0;
		for (i = 0; i < transitionClusters.size(); i++) {
			TransitionCluster trans = (TransitionCluster) transitionClusters
					.get(i);
			if (hideSimpleClusters && trans.size() < 2) {
				continue;
			}
			bw.write("subgraph \"cluster_" + trans.getLabel() + "\" {");
			bw
					.write("style=filled; fontname=\"Arial\";fillcolor=lightskyblue1; label=\"");
			bw.write(trans.getLabel() + "\"");
			// determine the places contained in the cluster
			ArrayList places = findPlaces(trans);
			Iterator it = places.iterator();
			while (it.hasNext()) {
				bw.write("; p" + ((Place) it.next()).getNumber());

			}
			// walk through the transitions contained in the cluster
			it = trans.iterator();
			while (it.hasNext()) {
				bw.write("; t" + ((Transition) it.next()).getNumber());
			}
			bw.write("}\n");
		}
	}

	/**
	 * This is the finishing part of the {@link #writeToDot writeToDot}
	 * procedure. Here the graph is only closed but overriding classes might
	 * want to add additional visualization elements after writing the acutal
	 * Petri net.
	 * 
	 * @param bw
	 *            the writer used by the framework to create the temporary dot
	 *            file
	 * @throws IOException
	 *             if writing to the writer fails
	 */
	protected void finishDotWriting(Writer bw) throws IOException {
		// close the graph
		bw.write("}\n");
	}

	/**
	 * Adds a token to the given place in this Petri net. If the given place is
	 * contained in this Petri net, a new token will be created with the given
	 * time stamp and attached to it.
	 * 
	 * @param timestamp
	 *            the date to be attached with the token
	 * @param p
	 *            the place where the token should be added
	 * @return <code>true</code> if the token could be added, <code>false</code>
	 *         otherwise
	 */
	public boolean addToken(Date timestamp, Place p) {
		if (!getVerticeList().contains(p)) {
			return false;
		}
		p.addToken(new Token(timestamp));
		return true;
	}

	/**
	 * Finds a transition having the specified log event.
	 * <p>
	 * Use this method if you know that there are no multiple transitions
	 * associated to the same log event (i.e., duplicate tasks), or if finding
	 * one of them would suffice.
	 * <p>
	 * If you want to get a complete list of transitions having the specified
	 * log event, use {@link #findTransitions findTransitions} instead.
	 * 
	 * @param lme
	 *            the log event specified
	 * @return one transition associated to the given log event, if existent.
	 *         <code>Null</code> otherwise.
	 */
	public Transition findRandomTransition(LogEvent lme) {
		if (findTransitions(lme).size() == 0) {
			return null;
		} else {
			return (Transition) findTransitions(lme).get(0);
		}
	}

	/**
	 * Finds a random transition without having a log event.
	 * <p>
	 * Use this method if you know that there are no multiple transitions
	 * associated to the same log event (i.e., duplicate tasks), or if finding
	 * one of them would suffice.
	 * <p>
	 * If you want to get a complete list of transitions having the specified
	 * log event, use {@link #findTransitions findTransitions} instead.
	 * 
	 * @return one transition not associated to a log event, if existent.
	 *         <code>Null</code> otherwise.
	 */
	public Transition findRandomInvisibleTransition() {
		for (Transition t : getTransitions()) {
			if (t.getLogEvent() == null) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Finds all transitions having the specified log event.
	 * <p>
	 * Use this method if you expect the net having multiple transitions
	 * associated to the same log event (i.e., duplicate tasks).
	 * <p>
	 * If this cannot happen or finding one of them would suffice, you can use
	 * {@link #findRandomTransition findRandomTransition} instead, which will
	 * directly return the first element of that list.
	 * 
	 * @param lme
	 *            the log event specified
	 * @return a list of all transitions associated to the given log event. The
	 *         list may be empty.
	 */
	public ArrayList<Transition> findTransitions(LogEvent lme) {
		Iterator i = transitions.iterator();
		ArrayList<Transition> a = new ArrayList<Transition>();
		while (i.hasNext()) {
			Transition t = (Transition) (i.next());
			if (t.hasLogModelElement(lme)) {
				a.add(t);
			}
		}
		return a;
	}

	/**
	 * @deprecated Please use findTransition(Transition t) instead
	 * @see findTransition(Transition t) Finds a transition in the Petri net
	 *      with a link to the given LogEvent. In case there are more, it
	 *      returns the first
	 * 
	 * @param e
	 *            the LogEvent to compare with
	 * @return an equal transition, if existent. <code>Null</code> otherwise.
	 */
	public Transition findTransition(LogEvent lme) {
		Iterator i = transitions.iterator();
		while (i.hasNext()) {
			Transition t = (Transition) (i.next());
			if (t.hasLogModelElement(lme)) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Finds a transition in the Petri net which is equal to the given one
	 * (based on the {@link Transition#equals equals} method).
	 * 
	 * @param t
	 *            the transition to compare with
	 * @return an equal transition, if existent. <code>Null</code> otherwise.
	 */
	public Transition findTransition(Transition t) {
		Iterator i = transitions.iterator();
		while (i.hasNext()) {
			Transition t2 = (Transition) (i.next());
			if (t.equals(t2)) {
				return t2;
			}
		}
		return null;
	}

	/**
	 * Finds a transition in the Petri net based on a given identifier (which is
	 * assumed to be unique).
	 * 
	 * @param identifier
	 *            the string specifying the identifier
	 * @return the transition having the given identifier, if existent.
	 *         <code>Null</code> otherwise.
	 */
	public Transition findTransition(String identifier) {
		Iterator<Transition> allTransitions = this.getTransitions().iterator();
		while (allTransitions.hasNext()) {
			Transition t = allTransitions.next();
			if (t.getIdentifier().equals(identifier)) {
				return t;
			}
		}
		// not found
		return null;
	}

	/**
	 * Determines all places that are located between the transitions contained
	 * in a transition cluster.
	 * 
	 * @param transitions
	 *            the transition cluster for which the contained places should
	 *            be found
	 * @return the list of places contained in the given transition cluster
	 */
	private ArrayList<Place> findPlaces(TransitionCluster transitions) {
		ArrayList<Place> pl = new ArrayList<Place>();
		for (Place place : places) {
			Set<Transition> neighbors = place.getNeighbors();
			/*
			 * If there's only one neighbor, then this should be a source or
			 * sink place. Do not add source or sink places to any cluster.
			 * Otherwise, add the place if all its neighbors belong to the
			 * cluster.
			 */
			if ((neighbors.size() > 1) && transitions.containsAll(neighbors)) {
				pl.add(place);
			}
		}
		return pl;
	}

	/**
	 * Finds a place in the Petri net based on a given identifier (which is
	 * assumed to be unique).
	 * 
	 * @param identifier
	 *            the string specifying the identifier
	 * @return the place having the given identifier, if existent.
	 *         <code>Null</code> otherwise.
	 */
	public Place findPlace(String identifier) {
		Iterator<Place> allPlaces = this.getPlaces().iterator();
		while (allPlaces.hasNext()) {
			Place p = allPlaces.next();
			if (p.hasIdentifier(identifier)) {
				return p;
			}
		}
		// not found
		return null;
	}

	/**
	 * Finds a place in the Petri net based on a given set of predecessor and
	 * successor nodes.
	 * 
	 * @param in
	 *            the set of predecessor nodes
	 * @param out
	 *            the set of successor nodes
	 * @return the first place having the given set of predecessor and successor
	 *         nodes, if existent. <code>Null</code> otherwise.
	 */
	public Place findPlace(Set in, Set out) {
		Iterator<Place> allPlaces = this.getPlaces().iterator();
		while (allPlaces.hasNext()) {
			Place p = allPlaces.next();
			if ((p.getPredecessors().equals(in))
					&& (p.getSuccessors().containsAll(out))) {
				return p;
			}
		}
		// not found
		return null;
	}

	/**
	 * Finds an edge in the Petri net based on a given head and tail (which is
	 * assumed to be unique).
	 * 
	 * @param head
	 *            the node specifying the head of the edge
	 * @param tail
	 *            the node specifying the tail of the edge
	 * @return the edge having the given head and tail, if existent.
	 *         <code>Null</code> otherwise.
	 */
	public PNEdge findEdge(PNNode tail, PNNode head) {
		Iterator<PNEdge> allEdges = this.getEdges().iterator();
		while (allEdges.hasNext()) {
			PNEdge e = allEdges.next();
			if (e.getHead().equals(head) & e.getTail().equals(tail)) {
				return e;
			}
		}
		// not found
		return null;
	}

	/**
	 * Clusters transitions based on their LogEvents. All Transition with a
	 * LogEvent with the same name are clustered using the addCluster method.
	 */
	public void makeClusters() {

		int nme = transitions.size();

		DoubleMatrix1D clustered = DoubleFactory1D.dense.make(nme, 0);

		for (int i = 0; i < nme; i++) {
			if ((clustered.get(i) == 1)
					|| (((Transition) transitions.get(i)).getLogEvent() == null)) {
				continue;
			}
			IntArrayList trans = new IntArrayList();
			for (int j = i; j < nme; j++) {
				if (((Transition) transitions.get(j)).getLogEvent() == null) {
					continue;
				}
				if (!((Transition) transitions.get(i)).getLogEvent()
						.getModelElementName().equals(
								((Transition) transitions.get(j)).getLogEvent()
										.getModelElementName())) {
					continue;
				}
				clustered.set(j, 1);
				trans.add(j);
			}
			if (trans.size() >= 2) {
				TransitionCluster tc = new TransitionCluster(
						((Transition) transitions.get(trans.get(0)))
								.getLogEvent().getModelElementName());
				for (int j = 0; j < trans.size(); j++) {
					tc
							.addTransition((Transition) transitions.get(trans
									.get(j)));
				}
				addCluster(tc);
			}
		}
	}

	/**
	 * Builds the incidence matrix belonging to this Petri net.
	 * 
	 * @todo: Provide documentation
	 * 
	 * @return the incidence matrix belonging to this Petri net
	 */
	/*
	 * HV: Seems to be able to deal with weighted arcs (it increments and
	 * decrements).
	 */
	public DoubleMatrix2D getIncidenceMatrix() {
		DoubleMatrix2D matrix = DoubleFactory2D.sparse.make(transitions.size(),
				places.size(), 0);
		Iterator it = getEdges().iterator();
		while (it.hasNext()) {
			PNEdge e = (PNEdge) it.next();
			if (e.isPT()) {
				int p = places.indexOf(e.getSource());
				int t = transitions.indexOf(e.getDest());
				matrix.set(t, p, matrix.get(t, p) - 1);
			} else {
				int t = transitions.indexOf(e.getSource());
				int p = places.indexOf(e.getDest());
				matrix.set(t, p, matrix.get(t, p) + 1);
			}
		}
		// Message.add("transitions: " + transitions.size(), Message.DEBUG);
		// Message.add("places: " + places.size(), Message.DEBUG);
		// Message.add("Incidence matrix: " + matrix.toString(), Message.DEBUG);
		return matrix;
	}

	/**
	 * Builds the incidence matrix for the input arcs of transitions in this
	 * Petri net.
	 * 
	 * @todo: Provide documentation
	 * 
	 * @return the input arcs transitions incidence matrix belonging to this
	 *         Petri net
	 */
	/*
	 * HV: Seems to be able to deal with weighted arcs (it increments and
	 * decrements). AKAM: I had to build this to solve the situations with
	 * self-loops
	 */
	public DoubleMatrix2D getInputArcsTransitionsIncidenceMatrix() {
		DoubleMatrix2D matrix = DoubleFactory2D.sparse.make(transitions.size(),
				places.size(), 0);
		Iterator it = getEdges().iterator();
		while (it.hasNext()) {
			PNEdge e = (PNEdge) it.next();
			if (e.isPT()) {
				int p = places.indexOf(e.getSource());
				int t = transitions.indexOf(e.getDest());
				matrix.set(t, p, matrix.get(t, p) - 1);
			}
		}
		// Message.add("transitions: " + transitions.size(), Message.DEBUG);
		// Message.add("places: " + places.size(), Message.DEBUG);
		// Message.add("Incidence matrix: " + matrix.toString(), Message.DEBUG);
		return matrix;
	}

	/**
	 * Builds the incidence matrix for the output arcs of transitions in this
	 * Petri net.
	 * 
	 * @todo: Provide documentation
	 * 
	 * @return the output arcs transitions incidence matrix belonging to this
	 *         Petri net
	 */
	/*
	 * HV: Seems to be able to deal with weighted arcs (it increments and
	 * decrements). AKAM: I had to build this to solve the situations with
	 * self-loops
	 */

	public DoubleMatrix2D getOutputArcsTransitionsIncidenceMatrix() {
		DoubleMatrix2D matrix = DoubleFactory2D.sparse.make(transitions.size(),
				places.size(), 0);
		Iterator it = getEdges().iterator();
		while (it.hasNext()) {
			PNEdge e = (PNEdge) it.next();
			if (!e.isPT()) {
				int t = transitions.indexOf(e.getSource());
				int p = places.indexOf(e.getDest());
				matrix.set(t, p, matrix.get(t, p) + 1);
			}
		}
		// Message.add("transitions: " + transitions.size(), Message.DEBUG);
		// Message.add("places: " + places.size(), Message.DEBUG);
		// Message.add("Incidence matrix: " + matrix.toString(), Message.DEBUG);
		return matrix;
	}

	// ////////////////// MAPPING-RELATED METHODS
	// ///////////////////////////////

	/**
	 * Retrieves the number of transitions without counting invisible tasks,
	 * i.e., those that do not have a corresponding log event.
	 * 
	 * @return the number of visible transitions
	 */
	public int getNumberOfVisibleTasks() {
		Iterator allTransitions = getTransitions().iterator();
		int counter = 0;
		while (allTransitions.hasNext()) {
			Transition currentTransition = (Transition) allTransitions.next();
			if (currentTransition.isInvisibleTask() == false) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Retrieves the number of invisible tasks, i.e., those that do not have a
	 * corresponding log event.
	 * 
	 * @return the number of invisible transitions
	 */
	public int getNumberOfInvisibleTasks() {
		Iterator allTransitions = getTransitions().iterator();
		int counter = 0;
		while (allTransitions.hasNext()) {
			Transition currentTransition = (Transition) allTransitions.next();
			if (currentTransition.isInvisibleTask() == true) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Retrieves the invisible tasks contained in this net.
	 * 
	 * @return the list of those transitions that resemble invisible tasks. The
	 *         list may be empty if the model contains no invisible tasks
	 */
	public ArrayList<Transition> getInvisibleTasks() {
		ArrayList<Transition> result = new ArrayList<Transition>();
		Iterator allTransitions = getTransitions().iterator();
		while (allTransitions.hasNext()) {
			Transition currentTransition = (Transition) allTransitions.next();
			if (currentTransition.isInvisibleTask() == true) {
				result.add(currentTransition);
			}
		}
		return result;
	}

	/**
	 * Retrieves the visible tasks contained in this net.
	 * 
	 * @return the list of those transitions that resemble visible tasks. The
	 *         list may be empty if the model contains no visible tasks
	 */
	public ArrayList<Transition> getVisibleTasks() {
		ArrayList<Transition> result = new ArrayList<Transition>();
		Iterator allTransitions = getTransitions().iterator();
		while (allTransitions.hasNext()) {
			Transition currentTransition = (Transition) allTransitions.next();
			if (currentTransition.isInvisibleTask() == false) {
				result.add(currentTransition);
			}
		}
		return result;
	}

	/**
	 * Retrieves the number of visible tasks without counting duplicate tasks
	 * multiple times. Note that this corresponds to counting the number of
	 * different task labels.
	 * 
	 * @return int the number of non-duplicate tasks
	 */
	public int getNumberOfNonDuplicateTasks() {
		Iterator<Transition> allTransitions = getTransitions().iterator();
		// this list is to store those duplicates that should not be counted
		ArrayList<Transition> duplicateTasks = new ArrayList<Transition>();
		int counter = 0;
		while (allTransitions.hasNext()) {
			Transition currentTransition = allTransitions.next();
			// only count if visible and not yet counted other duplicate yet
			if (duplicateTasks.contains(currentTransition) == false
					&& currentTransition.isInvisibleTask() == false) {
				counter++;
				ArrayList<Transition> duplicates = this
						.findTransitions(currentTransition.getLogEvent());
				if (duplicates.size() > 1) {
					Iterator allDuplicates = duplicates.iterator();
					while (allDuplicates.hasNext()) {
						Transition currentDuplicate = (Transition) allDuplicates
								.next();
						duplicateTasks.add(currentDuplicate);
					}
				}
			}
		}
		return counter;
	}

	/**
	 * Count the Number of Duplicate tasks in the model. <br>
	 * For example, if there are three tasks that have the same label, and
	 * another two tasks that have the same lable, this metric returns 5.
	 * 
	 * @return the No. of duplicate tasks in the model
	 */
	public int getNumberOfDuplicateTasks() {
		int counter = 0;
		Iterator<Transition> allTransitions = getTransitions().iterator();
		while (allTransitions.hasNext()) {
			Transition trans = allTransitions.next();
			if (trans.isDuplicateTask() == true) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Determines whether two duplicate tasks with the same label share any
	 * input places. <br>
	 * This is a pre-condition that, e.g., needs to be checked for some of the
	 * benchmark metrics (as they otherwise yield invalid results).
	 * 
	 * @return <code>True</code> if there are duplicate tasks with shared input
	 *         places. <code>False</code> otherwise
	 */
	public boolean hasDuplicatesWithSharedInputPlaces() {
		boolean result = false;
		Iterator<Transition> allTransitions = getTransitions().iterator();
		ArrayList<Transition> alreadyChecked = new ArrayList<Transition>();

		duplSearch: // label to abort outmost loop as soon as one shared input
		// was found
		while (allTransitions.hasNext()) {
			Transition trans = allTransitions.next();
			if (alreadyChecked.contains(trans) == false
					&& trans.isDuplicateTask() == true) {
				// find all other duplicate tasks with this label
				LogEvent le = trans.getLogEvent();
				ArrayList<Transition> allDuplicates = this.findTransitions(le);
				// remove task itself (as otherwise will always find shared
				// input to itself)
				// and remember in- and output places
				allDuplicates.remove(trans);
				Set<Place> inPlaces = trans.getPredecessors();
				Set<Place> outPlaces = trans.getSuccessors();
				Iterator<Transition> duplToBeChecked = allDuplicates.iterator();
				while (duplToBeChecked.hasNext()) {
					Transition dupl = duplToBeChecked.next();
					// check for shared inputs
					Iterator<Place> inputs = dupl.getPredecessors().iterator();
					while (inputs.hasNext()) {
						Place inPl = inputs.next();
						if (inPlaces.contains(inPl) == true) {
							result = true;
							break duplSearch;
						}
					}
				}
				// avoid to re-check these duplicate tasks
				alreadyChecked.addAll(allDuplicates);
			}
		}
		// return whether duplicates with shared input place was found or not
		return result;
	}

	/**
	 * Returns the first source found in the nodes of this Petri net
	 * 
	 * @return A source of this Petri net
	 */
	public ModelGraphVertex getSource() {
		for (Place place : places)
			if (place.inDegree() == 0)
				return place;
		for (Transition transition : transitions)
			if (transition.inDegree() == 0)
				return transition;

		return null;
	}

	/**
	 * Returns the first sink found in the nodes of this Petri net
	 * 
	 * @return A sink of this Petri net
	 */
	public ModelGraphVertex getSink() {
		for (Place place : places)
			if (place.outDegree() == 0)
				return place;
		for (Transition transition : transitions)
			if (transition.outDegree() == 0)
				return transition;

		return null;
	}

	/**
	 * Returns the a cloned subnet of this PetriNet containing the nodes of the
	 * input set argument. Only edges that has both head and tail that belongs
	 * to the input set are included.
	 * 
	 * @param A
	 *            set of nodes that belongs to this PetriNet
	 * @return A cloned PetriNet containing only nodes of the input set
	 */
	public PetriNet extractNet(Set<Node> nodes) {
		PetriNet o = null;
		o = (PetriNet) super.clone();

		// reset lists as so far they still point to the same ones as this
		// object does
		o.transitions = new ArrayList<Transition>();
		o.places = new ArrayList<Place>();
		o.transitionClusters = new ArrayList<TransitionCluster>();

		Set<String> nodeNames = new LinkedHashSet<String>();
		for (Node node : nodes) {
			nodeNames.add(node.getName());
		}

		// establish the same petri net structure like in this object
		Iterator transitions = this.getTransitions().iterator();
		HashMap<Transition, Transition> mapping = new HashMap<Transition, Transition>();
		while (transitions.hasNext()) {
			Transition transition = (Transition) transitions.next();
			if (nodeNames.contains(transition.getName())) {
				Transition clonedTransition = (Transition) transition.clone();
				// cloned transition must be told that it belongs to the cloned
				// net
				o.addAndLinkTransition(clonedTransition);
				// keep the mapping until the edges have been established
				mapping.put(transition, clonedTransition);
			}
		}

		Iterator places = this.getPlaces().iterator();
		while (places.hasNext()) {
			Place place = (Place) places.next();
			if (nodeNames.contains(place.getName())) {
				Place clonedPlace = (Place) place.clone();
				// cloned place must be told that it belongs to the cloned net
				o.addAndLinkPlace(clonedPlace);
			}
		}

		Iterator edges = this.getEdges().iterator();
		while (edges.hasNext()) {
			PNEdge edge = (PNEdge) edges.next();
			if (nodeNames.contains(edge.getHead().getName())
					&& nodeNames.contains(edge.getTail().getName())) {
				PNEdge clonedEdge = (PNEdge) edge.clone();
				// if place is source
				if (edge.isPT()) {
					Place p = (Place) edge.getSource();
					// find respective place in this net (place names are
					// assumed to
					// be unique)
					Place myPlace = (Place) o.findPlace(p.getIdentifier());
					Transition t = (Transition) edge.getDest();
					// find respective transition in this net
					Transition myTransition = (Transition) mapping.get(t);
					// establish cloned place/transition as new source/target
					o.addAndLinkEdge(clonedEdge, myPlace, myTransition);
				}
				// if transition is source
				else {
					Place p = (Place) edge.getDest();
					// find respective place in this net (place names are
					// assumed to
					// be unique)
					Place myPlace = (Place) o.findPlace(p.getIdentifier());
					Transition t = (Transition) edge.getSource();
					// find respective transition in this net
					Transition myTransition = (Transition) mapping.get(t);
					// establish cloned transition/place as new source/target
					o.addAndLinkEdge(clonedEdge, myTransition, myPlace);
				}
			}
		}

		Iterator clusters = this.getClusters().iterator();
		while (clusters.hasNext()) {
			TransitionCluster cluster = (TransitionCluster) clusters.next();
			TransitionCluster clonedCluster = (TransitionCluster) cluster
					.clone();
			List<Transition> removeNodes = new LinkedList<Transition>();
			for (Object transition : clonedCluster)
				if (!mapping.containsValue(transition))
					removeNodes.add((Transition) transition);
			for (Transition transition : removeNodes)
				clonedCluster.remove(transition);
			if (!clonedCluster.isEmpty())
				o.addCluster(clonedCluster);
		}
		return o;
	}

	/**
	 * Returns the number of places in this PetriNet
	 * 
	 * @return The number of places
	 */
	public int numberOfPlaces() {
		return places.size();
	}

	/**
	 * Returns the number of transitions in this PetriNet
	 * 
	 * @return The number of transitions
	 */
	public int numberOfTransitions() {
		return transitions.size();
	}

	/**
	 * Returns the number of nodes in this PetriNet, i.e., transitions plus
	 * places
	 * 
	 * @return The number of nodes
	 */
	public int numberOfNodes() {
		return places.size() + transitions.size();
	}

	/**
	 * Determines if this net is a marked graph, i.e., each place has at most
	 * one input transition and at most one output transition.
	 * 
	 * @return True if the net is a marked graph and false otherwise
	 */
	public boolean isMarkedGraph() {
		ArrayList list = new ArrayList();
		Iterator<Place> allPlaces = this.getPlaces().iterator();
		while (allPlaces.hasNext()) {
			Place p = allPlaces.next();
			if (p.getPredecessors().size() <= 1
					&& p.getSuccessors().size() <= 1) {
				list.add(p);
			}
		}
		return list.size() == this.getPlaces().size();
	}
	//TSJ
    public int getNumOfCycle(){
    	int num = 0;
    	List<Node> cycleNodes = new LinkedList<Node>();
		Queue<Node> queue = new LinkedList<Node>();
		List<Node> visitedNodes = new ArrayList<Node>();
		for(Node node : getNodes()){
			Node ini = node;
			queue.add(node);
			visitedNodes= new ArrayList<Node>();
			while(!queue.isEmpty()){
				Node x = queue.remove();
				visitedNodes.add(x);
				List<Edge> outgoingArcs = x.getOutEdges();
				if (outgoingArcs == null)
					outgoingArcs = new LinkedList<Edge>();
				for (Edge edge : outgoingArcs){
//					edge.getTail()
					Node opposite = edge.getHead();
//					Node opposite = edge.getOpposite(node);
					if(opposite.equals(ini)){
						cycleNodes.add(opposite);
					}
					if(!visitedNodes.contains(opposite)){
						queue.add(opposite);
					}
				}
			}
		}
		num = cycleNodes.size();
		return num;
	}

	/**
	 * Determines if this net is acyclic
	 * 
	 * @return True if the net is acyclic and false otherwise
	 */
	public boolean isAcyclic() {
		if (getSource() == null)
			return false;
		List<Node> sortedNodes = topologicalSort(extractNet(new LinkedHashSet<Node>(
				getNodes())));
		return sortedNodes.size() == getNodes().size();
	}

	/**
	 * Implementation of an topological sort. If the algorithm can not find a
	 * topological order it is because the PetriNet is cyclic
	 * 
	 * @param A
	 *            PetriNet
	 * @return A list that gives the topological order of the input PetriNet
	 */
	private List<Node> topologicalSort(PetriNet pn) {
		List<Node> sortedNodes = new LinkedList<Node>();
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(pn.getSource());

		while (!queue.isEmpty()) {
			Node node = queue.remove();
			sortedNodes.add(node);
			List<Edge> outgoingArcs = node.getOutEdges();
			if (outgoingArcs == null)
				outgoingArcs = new LinkedList<Edge>();
			for (Edge edge : outgoingArcs) {
				pn.removeEdge((PNEdge) edge);
				Node opposite = edge.getOpposite(node);
				if (opposite.inDegree() == 0)
					queue.add(opposite);
			}
		}

		return sortedNodes;
	}

	/**
	 * @see org.processmining.framework.models.ModelGraph#removeVertex(org.processmining.framework.models.ModelGraphVertex)
	 */
	public void removeVertex(ModelGraphVertex v) {
		super.removeVertex(v);
		if (v instanceof Place)
			places.remove(v);
		else {
			List<TransitionCluster> list = new LinkedList<TransitionCluster>();
			for (TransitionCluster transitionCluster : transitionClusters)
				if (transitionCluster.contains(v))
					list.add(transitionCluster);
			transitionClusters.removeAll(list);
			transitions.remove(v);
		}
	}

	// //////////////// GENERAL PURPOSE METHODS
	// /////////////////////////////////

	/**
	 * Makes a deep copy of the object, i.e., reconstructs the Petri net
	 * structure with cloned transitions, places, edges, and clusters. Note that
	 * this method needs to be extended as soon as there are attributes added to
	 * the class which are not primitive or immutable.
	 * 
	 * @return the cloned object
	 */
	public Object clone() {

		PetriNet o = null;
		o = (PetriNet) super.clone();

		// reset lists as so far they still point to the same ones as this
		// object does
		o.transitions = new ArrayList();
		o.places = new ArrayList();
		o.transitionClusters = new ArrayList();

		// establish the same petri net structure like in this object
		Iterator transitions = this.getTransitions().iterator();
		HashMap mapping = new HashMap();
		while (transitions.hasNext()) {
			Transition transition = (Transition) transitions.next();
			Transition clonedTransition = (Transition) transition.clone();
			// cloned transition must be told that it belongs to the cloned net
			o.addAndLinkTransition(clonedTransition);
			// keep the mapping until the edges have been established
			mapping.put(transition, clonedTransition);
		}

		Iterator places = this.getPlaces().iterator();
		while (places.hasNext()) {
			Place place = (Place) places.next();
			Place clonedPlace = (Place) place.clone();
			// cloned place must be told that it belongs to the cloned net
			o.addAndLinkPlace(clonedPlace);
		}

		Iterator edges = this.getEdges().iterator();
		while (edges.hasNext()) {
			PNEdge edge = (PNEdge) edges.next();
			PNEdge clonedEdge = (PNEdge) edge.clone();
			// if place is source
			if (edge.isPT()) {
				Place p = (Place) edge.getSource();
				// find respective place in this net (place names are assumed to
				// be unique)
				Place myPlace = (Place) o.findPlace(p.getIdentifier());
				Transition t = (Transition) edge.getDest();
				// find respective transition in this net
				Transition myTransition = (Transition) mapping.get(t);
				// establish cloned place/transition as new source/target
				o.addAndLinkEdge(clonedEdge, myPlace, myTransition);
			}
			// if transition is source
			else {
				Place p = (Place) edge.getDest();
				// find respective place in this net (place names are assumed to
				// be unique)
				Place myPlace = (Place) o.findPlace(p.getIdentifier());
				Transition t = (Transition) edge.getSource();
				// find respective transition in this net
				Transition myTransition = (Transition) mapping.get(t);
				// establish cloned transition/place as new source/target
				o.addAndLinkEdge(clonedEdge, myTransition, myPlace);
			}
		}

		Iterator clusters = this.getClusters().iterator();
		while (clusters.hasNext()) {
			TransitionCluster cluster = (TransitionCluster) clusters.next();
			TransitionCluster clonedCluster = (TransitionCluster) cluster
					.clone();
			o.addCluster(clonedCluster);
		}
		return o;
	}

	/**
	 * Print key indicators of the PetriNet to the Test tab.
	 * 
	 * @param tag
	 *            String The tag to use for the indicators.
	 */
	public void Test(String tag) {
		Message.add("<" + tag + " nofTransitions=\"" + getTransitions().size()
				+ "\" nofPlaces=\"" + getPlaces().size() + "\" nofArcs=\""
				+ getEdges().size() + "\">", Message.TEST);
	}

	public int getCaseid() {
		return caseid;
	}

	public void setCaseid(int caseid) {
		this.caseid = caseid;
	}

	public boolean isTransitionEnable(Transition t) {
		HashSet set = t.getPredecessors();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Place p = (Place) it.next();
			if (p.getNumberOfTokens() <= 0)
				return false;
		}
		return true;
	}

	public void executeTransition(Transition t) {
		HashSet set = t.getPredecessors();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Place p = (Place) it.next();
			p.removeAllTokens();
		}
		set = t.getSuccessors();
		it = set.iterator();
		while (it.hasNext()) {
			Place p = (Place) it.next();
			p.removeAllTokens();
			p.addToken(new Token());
		}
	}

	public void initialMarking(int[] initialMarks) {
		for (int i = 0; i < places.size(); i++) {
			Place p = places.get(i);
			p.removeAllTokens();
			for (int j = 0; j < initialMarks[i]; j++) {
				p.addToken(new Token());
			}
		}
	}
	
	

	public int[][] getPTMatrix() {
		int[][] matrix = new int[places.size()][transitions.size()];
		Iterator it = getEdges().iterator();
		while (it.hasNext()) {
			PNEdge e = (PNEdge) it.next();
			if (e.isPT()) {
				int p = places.indexOf(e.getSource());
				int t = transitions.indexOf(e.getDest());
				matrix[p][t] += -1;
			} else {
				int t = transitions.indexOf(e.getSource());
				int p = places.indexOf(e.getDest());
				matrix[p][t] += 1;
			}
		}
		return matrix;
	}

	public void destroyPetriNet() {
		for (TransitionCluster tc : transitionClusters) {
			tc.clear();
		}
		for (Transition t : transitions) {
			t.destroyVertex();
		}
		for (Place p : places) {
			p.destroyVertex();
		}
		transitions = null;
		places = null;
		transitionClusters = null;

		this.clearModelGraph();
		this.clearGGraph();
		this.clearSubgraph();
		this.clearElement();
	}

	public ModelGraphPanel getGrappaVisualization(ResourcePetriNet resourcePetriNet) {
		BufferedWriter bw;
		Graph graph;
		File dotFile;

		try {
			// create temporary DOT file
			dotFile = File.createTempFile("pmt", ".dot");
			// dotFile.deleteOnExit();
			bw = new BufferedWriter(new FileWriter(dotFile, false));
			writeToDot(bw,resourcePetriNet);
			bw.close();

			// execute dot and parse the output of dot to create a Graph
			Message.add("Parsing DOT-file: " + dotFile.getAbsolutePath(),
					Message.DEBUG);
			graph = Dot.execute(dotFile.getAbsolutePath());
			dotFile.delete();

			visualObject = graph;
			Iterator it = getVerticeList().iterator();
			while (it.hasNext()) {
				((ModelGraphVertex) it.next()).visualObject = null;
			}
			it = getEdges().iterator();
			while (it.hasNext()) {
				((ModelGraphEdge) it.next()).visualObject = null;
			}

			if (graph == null) {
				return null;
			}

		} catch (Exception ex) {
			Message.add("Error while performing graph layout: "
					+ ex.getMessage(), Message.ERROR);
			return null;
		}

		buildNodeMapping(graph);
		buildEdgeMapping(graph);

		// adjust some settings
		graph.setEditable(true);
		graph.setMenuable(true);
		graph.setErrorWriter(new PrintWriter(System.err, true));

		// create the visual component and return it
		ModelGraphPanel gp = new ModelGraphPanel(graph, this);

		gp.setScaleToFit(true);

		gp.addGrappaListener(new GrappaAdapter());

		return gp;
		
	}

	private void writeToDot(BufferedWriter bw, ResourcePetriNet resourcePetriNet) throws IOException  {
		initDotWriting(bw,resourcePetriNet);
		writeTransitionsToDot(bw, resourcePetriNet);
		writePlacesToDot(bw);
		writeEdgesToDot(bw);
		writeClustersToDot(bw);
		finishDotWriting(bw);		
	}
	
	
	private void writeTransitionsToDot(BufferedWriter bw,
			ResourcePetriNet resourcePetriNet) throws IOException {
		Iterator it = getTransitions().iterator();
		while (it.hasNext()) {
			Transition t = (Transition) (it.next());
			ResourceTransition rT = resourcePetriNet.getResourceTransition(t);
			String label = t.getIdentifier();
			// write to dot
			bw.write("t"
					+ t.getNumber()
					+ " [shape=\"box\""
					+ (t.getLogEvent() != null ? ",label=\"" + label +"\\n"+rT.getRoles().toString()+ "\""
							: ",label=\"\",style=\"filled\"") + "];\n");
			// connect Petri net nodes to later grappa components
			nodeMapping.put(new String("t" + t.getNumber()), t);
		}
		
	}

	protected void initDotWriting(Writer bw, ResourcePetriNet resourcePetriNet) throws IOException {
		// write general settings to the dot file
		bw
				.write("digraph G {ranksep=\".3\"; fontsize=\"8\"; remincross=true; margin=\"0.0,0.0\"; ");
		bw.write("fontname=\"FangSong\";rankdir=\"LR\"; \n");
		bw.write("edge [arrowsize=\"0.5\"];\n");
		bw
				.write("node [height=\".2\",width=\".2\",fontname=\"FangSong\",fontsize=\"8\"];\n");
		// used to connect Petri net nodes to later grappa components
		nodeMapping.clear();
		// assign unique number to every transition
		// (needed when writing the edges as the identifier might be identical
		// for two transitions)
		Iterator it = getTransitions().iterator();
		int i = 0;
		while (it.hasNext()) {
			Transition t = (Transition) (it.next());
			t.setNumber(i);
			i++;
		}
		// assign unique number to every place
		it = getPlaces().iterator();
		i = 0;
		while (it.hasNext()) {
			Place p = (Place) (it.next());
			p.setNumber(i);
			i++;
		}
	}
	
}
