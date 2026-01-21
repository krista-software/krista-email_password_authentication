[<span style = "color : blue ; text-decoration: none">< back<span>](/)

# Configuring Email Password Authentication Extension in Krista

Perform the following steps to configure Email Password Authentication Extension in Krista.

### Step 1
Navigate to `Setup` > `Authentication` > `Email Password`.
![Routing Id Reference](../screenshot/setup.png)

### Step 2
On the next pop-up window, click Confirm. This will enable the selection of authentication method through Email Password.
![Routing Id Reference](../screenshot/confirm.png)

### Step 3
The following configuration parameters are also known as Invoker Attributes for authentication. Authentication processes are determined based on the values of these parameters.

1.	**Use Custom SMTP Settings:** Enable this option to configure custom SMTP server settings.
2.	**SMTP Host:** Specify the hostname or IP address of your SMTP server.
3.	**SMTP Port:** Enter the port number used for SMTP communication (e.g., 587 for TLS/STARTTLS, or 465 for SSL).
4.	**SMTP Username:** Provide the username for authenticating with the SMTP server.
5.	**SMTP Password:** Enter the password associated with the SMTP username.
6.	**SMTP From Address:** Set the email address that will appear as the sender.
7.	**Use SSL:** Enable this option if your SMTP server requires SSL encryption for communication.

Refer to the following screenshot:
![Routing Id Reference](../screenshot/attributes.png)