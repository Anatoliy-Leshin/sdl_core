/**
 * Copyright (c) 2013, Ford Motor Company
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of the Ford Motor Company nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

#include "application_manager/commands/hmi/on_button_press_notification.h"
#include "application_manager/application_manager_impl.h"
#include "application_manager/application_impl.h"
#include "application_manager/message_helper.h"
#include "utils/logger.h"

namespace application_manager {

namespace commands {

OnButtonPressNotification::OnButtonPressNotification(
    const MessageSharedPtr& message): NotificationFromHMI(message) {
}

OnButtonPressNotification::~OnButtonPressNotification() {
}

void OnButtonPressNotification::Run() {
  LOG4CXX_INFO(logger_, "OnButtonPressNotification::Run");

  if((*message_)[strings::msg_params].keyExists(
      hmi_response::custom_button_id)) {

    ApplicationImpl* app = static_cast<ApplicationImpl*>(
        ApplicationManagerImpl::instance()->active_application());

    if (NULL == app) {
      LOG4CXX_ERROR_EXT(logger_, "NULL pointer");
      return;
    }

    NotifyMobileApp(app);
    return;
  }

  const unsigned int btn_id = static_cast<unsigned int>(
      (*message_)[strings::msg_params]
      [hmi_response::button_name].asInt());

  const std::vector<Application*>& subscribedApps =
      ApplicationManagerImpl::instance()->applications_by_button(btn_id);

  std::vector<Application*>::const_iterator it = subscribedApps.begin();
  for (; subscribedApps.end() != it; ++it) {
    ApplicationImpl* subscribed_app = static_cast<ApplicationImpl*>(*it);

    if (subscribed_app) {
      NotifyMobileApp(subscribed_app);
    }
  }
}

void OnButtonPressNotification::NotifyMobileApp(ApplicationImpl* const app) {
  MessageHelper::SendHMIStatusNotification(*app);
}

}  // namespace commands

}  // namespace application_manager
