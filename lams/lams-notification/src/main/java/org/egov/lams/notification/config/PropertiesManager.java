package org.egov.lams.notification.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Setter
@Getter
public class PropertiesManager {

	@Autowired
	private Environment environment;

	@Value("${kafka.topics.notification.sms.name}")
	private String smsNotificationTopic;

	@Value("${lams.notification.sms.msg}")
	private String notificationMessage;

	@Value("${lams.approval.sms.msg}")
	public String approveMessage;

	@Value("${lams.rejected.sms.msg}")
	public String rejectMessage;

}
