/*

 Copyright (c) 2013, Ford Motor Company
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following
 disclaimer in the documentation and/or other materials provided with the
 distribution.

 Neither the name of the Ford Motor Company nor the names of its contributors
 may be used to endorse or promote products derived from this software
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */

#include "application_manager/commands/mobile/get_dtcs_request.h"
#include "application_manager/application_manager_impl.h"
#include "application_manager/application_impl.h"
#include "interfaces/HMI_API.h"

namespace application_manager {

namespace commands {

GetDTCsRequest::GetDTCsRequest(
    const MessageSharedPtr& message): CommandRequestImpl(message) {
}

GetDTCsRequest::~GetDTCsRequest() {
}

void GetDTCsRequest::Run() {
  LOG4CXX_INFO(logger_, "GetDTCsRequest::Run");

  ApplicationImpl* app = static_cast<ApplicationImpl*>(
      ApplicationManagerImpl::instance()->
      application((*message_)[strings::params][strings::connection_key]));

  if (NULL == app) {
    LOG4CXX_ERROR(logger_, "NULL pointer");
    SendResponse(false, mobile_apis::Result::APPLICATION_NOT_REGISTERED);
    return;
  }

  if (mobile_api::HMILevel::HMI_NONE == app->hmi_level()) {
    LOG4CXX_ERROR(logger_, "App has not been activated");
    SendResponse(false, mobile_apis::Result::REJECTED);
    return;
  }

  smart_objects::CSmartObject* vi_request  =
          new smart_objects::CSmartObject();

  if (NULL == vi_request) {
    LOG4CXX_ERROR(logger_, "NULL pointer");
    SendResponse(false, mobile_apis::Result::OUT_OF_MEMORY);
    return;
  }

  const int correlation_id =
      (*message_)[strings::params][strings::correlation_id];
  const int connection_key =
      (*message_)[strings::params][strings::connection_key];
  const int hmi_request_id = hmi_apis::FunctionID::VehicleInfo_GetDTCs;

  (*vi_request)[strings::params][strings::correlation_id] =
      correlation_id;

  (*vi_request)[strings::params][strings::function_id] =
      hmi_request_id;

  (*vi_request)[strings::params][strings::message_type] =
      MessageType::kRequest;

  (*vi_request)[strings::msg_params][strings::ecu_name] =
      (*message_)[strings::msg_params][strings::ecu_name];

  (*vi_request)[strings::msg_params][strings::dtc_mask] =
      (*message_)[strings::msg_params][strings::dtc_mask];

  (*vi_request)[strings::msg_params][strings::app_id] =
      app->app_id();

  ApplicationManagerImpl::instance()->AddMessageChain(NULL,
        connection_key, correlation_id, hmi_request_id, &(*vi_request));

  ApplicationManagerImpl::instance()->ManageHMICommand(message_);
}

}  // namespace commands

}  // namespace application_manager