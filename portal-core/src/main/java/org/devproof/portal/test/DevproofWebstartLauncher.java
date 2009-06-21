/*
 * Copyright 2009 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class DevproofWebstartLauncher extends JFrame {
	private static final long serialVersionUID = 1L;

	private JettyWebstart jettyWebstart;
	private JButton startButton;
	private JButton stopButton;

	public static void main(final String[] args) {
		new DevproofWebstartLauncher();
	}

	public DevproofWebstartLauncher() {
		super("Devproof Portal");
		this.jettyWebstart = new JettyWebstart();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		add(new JLabel(" Devproof Portal"), BorderLayout.NORTH);
		StringBuilder buf = new StringBuilder();
		buf.append("\n");
		buf.append("  Start the browser and open URL: http://localhost:8888/\n");
		buf.append("  Username: admin     Password: 12345\n\n");
		buf.append("  Download the portal: http://portal.devproof.org\n\n");
		buf.append("       --- devproof.org ---");

		JTextArea hint = new JTextArea(buf.toString());
		hint.setOpaque(false);
		hint.setEditable(false);
		add(hint, BorderLayout.CENTER);

		JPanel bottomButtons = new JPanel();
		bottomButtons.add(this.startButton = new JButton("Start Server"), BorderLayout.WEST);
		bottomButtons.add(this.stopButton = new JButton("Stop Server"), BorderLayout.EAST);
		add(bottomButtons, BorderLayout.SOUTH);
		setLocation(400, 300);
		setSize(400, 200);
		this.startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				startServer();
			}

		});

		this.stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				stopServer();
			}
		});
		setVisible(true);
		startServer();
	}

	private void startServer() {
		DevproofWebstartLauncher.this.startButton.setEnabled(false);
		DevproofWebstartLauncher.this.stopButton.setEnabled(true);
		DevproofWebstartLauncher.this.jettyWebstart.startServer(8888);
	}

	private void stopServer() {
		DevproofWebstartLauncher.this.startButton.setEnabled(true);
		DevproofWebstartLauncher.this.stopButton.setEnabled(false);
		DevproofWebstartLauncher.this.jettyWebstart.stopServer();
	}
}
