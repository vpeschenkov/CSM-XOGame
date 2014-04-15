package org.ultimate.server;

public interface ClientListener {
	void sendMessage(String message, long id);

	void deleteClient(long id);

	boolean addClient(long id, Client client);
}