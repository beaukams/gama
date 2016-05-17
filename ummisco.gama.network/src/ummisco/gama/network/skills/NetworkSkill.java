/*********************************************************************************************
 * 
 * 
 * 'Communication.java', in plugin 'ummisco.gama.communicator', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.network.skills;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@vars({ @var(name = INetworkSkill.NET_AGENT_NAME, type = IType.STRING, doc = @doc("Net ID of the agent")),
@var(name = INetworkSkill.NET_AGENT_GROUPS, type = IType.LIST, doc = @doc("Net ID of the agent")),
@var(name = INetworkSkill.NET_AGENT_SERVER, type = IType.LIST, doc = @doc("Net ID of the agent"))})
@skill(name = INetworkSkill.NETWORK_SKILL, concept = { IConcept.NETWORK, IConcept.COMMUNICATION, IConcept.SKILL })
public class NetworkSkill  extends Skill implements INetworkSkill{

	private final HashMap<String, LinkedList<Map<String, Object>>> agentMessage;
	private final HashMap<String,IConnector> serverList;
	
	public NetworkSkill()
	{
		agentMessage = new HashMap<String, LinkedList<Map<String, Object>>>();
		serverList = new HashMap<String,IConnector>();
	}

	
	@action(name = INetworkSkill.CONNECT_TOPIC, args = {
		@arg(name = INetworkSkill.PROTOCOL, type = IType.STRING, doc = @doc("protocol type (udp, tcp, mqqt)")),
		@arg(name = INetworkSkill.PORT, type = IType.STRING, doc = @doc("port number")),
		@arg(name = INetworkSkill.WITHNAME, type = IType.STRING, optional = true, doc = @doc("server nameL")),
		@arg(name = INetworkSkill.SERVER_URL, type = IType.STRING, optional = false, doc = @doc("server URL")) }, doc = @doc(value = "", returns = "", examples = { @example("") }))
	public void connectToServer(final IScope scope) throws GamaRuntimeException {
		String serverURL = (String) scope.getArg(INetworkSkill.SERVER_URL, IType.STRING);
		String dest = (String) scope.getArg(INetworkSkill.WITHNAME, IType.STRING);
		IConnector connector =  serverList.get(serverURL);
		if(connector == null)
		{
			if(INetworkSkill.PROTOCOL == "udp"){
				System.out.println("create udp serveur");
				connector = new UDPConnector();
			}

			if(INetworkSkill.PROTOCOL == "tcp"){
				System.out.println("create tcp serveur");
				connector = new TCPConnector();
			}
			
			if(INetworkSkill.PROTOCOL == "mqtt"){
				System.out.println("create mqtt serveur");
				connector = new MQTTConnectorSk();
			}			
		    serverList.put(serverURL,connector);
		}
		scope.getAgentScope().setAttribute(NET_AGENT_NAME, dest);
		scope.getAgentScope().setAttribute(NET_AGENT_SERVER, serverURL);
		try {
			connector.connectToServer(scope.getAgentScope(), dest, serverURL, scope);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@action(name = INetworkSkill.SEND_MESSAGE, args = {
		@arg(name = INetworkSkill.TO, type = IType.STRING, optional = true, doc = @doc("The network ID of the agent who receive the message")),
		@arg(name = INetworkSkill.CONTENT, type = IType.NONE, optional = true, doc = @doc("The content of the message")) }, doc = @doc(value = "Send a message to a destination.", returns = "the path followed by the agent.", examples = { @example(value = "do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}") }))
	public void sendMessage(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		String dest = (String) scope.getArg(INetworkSkill.TO, IType.STRING);
		String serverName = (String)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		String sender = (String) agent.getAttribute(INetworkSkill.NET_AGENT_NAME);
		Object messageContent = scope.getArg(INetworkSkill.CONTENT, IType.NONE);
		IConnector connector=this.serverList.get(serverName);
		HashMap<String, String> mp = new HashMap<>();
		mp.put(INetworkSkill.FROM,sender);
		mp.put(INetworkSkill.CONTENT,messageContent.toString());
		System.out.println("sender "+sender + " message"+mp);
		connector.sendMessage(dest, mp);
	}

	@action(name = INetworkSkill.FETCH_MESSAGE, args = {}, doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "the path followed by the agent.", examples = { @example("do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}") }))
	public GamaMap<String, String> fetchMessage(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		String serverName = (String)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		IConnector connector=this.serverList.get(serverName);
		GamaMap<String, String>  res = connector.fetchMessageBox(agent);
		return res; //connector.fetchMessageBox(agent); //fetchMyMailBox(scope, tmpName);

	}

	@action(name = INetworkSkill.HAS_MORE_MESSAGE_IN_BOX, args = {}, doc = @doc(value = "moves the agent towards the target passed in the arguments.", returns = "the path followed by the agent.", examples = { @example("do action: goto{\n arg target value: one_of (list (species (self))); \n arg speed value: speed * 2; \n arg on value: road_network;}") }))
	public boolean notEmptyMessageBox(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		String serverName = (String)  agent.getAttribute(INetworkSkill.NET_AGENT_SERVER);
		IConnector connector=this.serverList.get(serverName);
		
		return !connector.emptyMessageBox(agent); //isEmptyMailBox(scope, tmpName);
	}

}