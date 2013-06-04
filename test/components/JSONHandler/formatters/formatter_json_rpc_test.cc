// Copyright (c) 2013, Ford Motor Company
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following
// disclaimer in the documentation and/or other materials provided with the
// distribution.
//
// Neither the name of the Ford Motor Company nor the names of its contributors
// may be used to endorse or promote products derived from this software
// without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 'A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include <iostream>

#include "JSONHandler/formatters/formatter_json_rpc.h"

#include "json/json.h"

#include "CFormatterTestHelper.hpp"

namespace test {
namespace components {
namespace JSONHandler {
namespace Formatters {

// The code below is cut and paste from the generated code just to provide necessary environment for string <-> enum conversion.
namespace FunctionID {
/**
 * @brief Enumeration FunctionID.
 */
enum eType {
  /**
   * @brief INVALID_ENUM.
   */
  INVALID_ENUM = -1,

  /**
   * @brief RegisterAppInterface.
   */
  RegisterAppInterface,

  /**
   * @brief UnregisterAppInterface.
   */
  UnregisterAppInterface,

  /**
   * @brief SetGlobalProperties.
   */
  SetGlobalProperties,

// ...
};
}

namespace messageType {
/**
 * @brief Enumeration messageType.
 */
enum eType {
  /**
   * @brief INVALID_ENUM.
   */
  INVALID_ENUM = -1,

  /**
   * @brief request.
   */
  request,

  /**
   * @brief response.
   */
  response,

  /**
   * @brief notification.
   */
  notification
};
}
// end of cut and pasted code
using namespace NsSmartDeviceLink::NsJSONHandler::strings;
typedef NsSmartDeviceLink::NsJSONHandler::Formatters::FormatterJsonRpc JSONFormatter;

TEST_F(CFormatterTestHelper, test_JsonRPC2) {
  Json::Value value;  // just a quick workaround to avoid undefined reference to Json
  Json::Reader reader;    // the same thing

  std::string str;
  NsSmartDeviceLink::NsSmartObjects::CSmartObject srcObj;
  NsSmartDeviceLink::NsSmartObjects::CSmartObject dstObj;

  // TODO: Implement test on valid RPC2 test data

  srcObj[S_PARAMS][S_MESSAGE_TYPE] = messageType::request;
  srcObj[S_PARAMS][S_FUNCTION_ID] = FunctionID::RegisterAppInterface;
  srcObj[S_PARAMS][S_PROTOCOL_VERSION] = 2;
  srcObj[S_PARAMS][S_PROTOCOL_TYPE] = 1;
  srcObj[S_PARAMS][S_CORRELATION_ID] = 12;
  srcObj[S_MSG_PARAMS]["appId"] = "APP ID";
  srcObj[S_MSG_PARAMS]["appName"] = "APP NAME";
  srcObj[S_MSG_PARAMS]["appType"][0] = "SYSTEM";
  srcObj[S_MSG_PARAMS]["appType"][1] = "COMMUNICATION";
  srcObj[S_MSG_PARAMS]["hmiDisplayLanguageDesired"] = "RU-RU";
  srcObj[S_MSG_PARAMS]["isMediaApplication"] = true;
  srcObj[S_MSG_PARAMS]["languageDesired"] = "EN-US";
  srcObj[S_MSG_PARAMS]["ngnMediaScreenAppName"] = "SCREEN NAME";
  srcObj[S_MSG_PARAMS]["syncMsgVersion"]["majorVersion"] = 2;
  srcObj[S_MSG_PARAMS]["syncMsgVersion"]["minorVersion"] = 10;
  srcObj[S_MSG_PARAMS]["ttsName"][0]["text"] = "ABC";
  srcObj[S_MSG_PARAMS]["ttsName"][0]["type"] = "TEXT";
  srcObj[S_MSG_PARAMS]["vrSynonyms"][0] = "Synonym1";
  srcObj[S_MSG_PARAMS]["vrSynonyms"][1] = "Synonym2";
  srcObj[S_MSG_PARAMS]["null"] =
    NsSmartDeviceLink::NsSmartObjects::CSmartObject();
  srcObj[S_MSG_PARAMS]["double"] = -0.1234;

  // SmartObjects --> JSON
  JSONFormatter::ToString(srcObj, str);

  std::cout << str << std::endl;

  // JSON --> SmartObjects
  JSONFormatter::FromString<FunctionID::eType, messageType::eType>(str, dstObj);

  // Compare SmartObjects
  ASSERT_EQ("APP NAME",
            static_cast<std::string>(dstObj["msg_params"]["appName"]));

  ASSERT_EQ(10,
            static_cast<int>(
                dstObj["msg_params"]["syncMsgVersion"]["minorVersion"]));

  ASSERT_EQ("TEXT",
            static_cast<std::string>(
                dstObj["msg_params"]["ttsName"][0]["type"]));

  ASSERT_TRUE(static_cast<bool>(dstObj["msg_params"]["isMediaApplication"]));

  compareObjects(srcObj, dstObj);
}

}  //namespace Formatters
}  //namespace JSONHandler
}  //namespace components
}  //namespace test


namespace func_id_ns = test::components::JSONHandler::Formatters::FunctionID;
namespace msg_type_ns = test::components::JSONHandler::Formatters::messageType;

namespace NsSmartDeviceLink {
namespace NsSmartObjects {

template<>
const std::map<func_id_ns::eType, std::string> &
NsSmartDeviceLink::NsSmartObjects::TEnumSchemaItem<func_id_ns::eType>::
getEnumElementsStringRepresentation(void) {
  static bool isInitialized = false;
  static std::map<func_id_ns::eType, std::string> enumStringRepresentationMap;

  if (false == isInitialized) {
    enumStringRepresentationMap.insert(
      std::make_pair(func_id_ns::RegisterAppInterface,
                     "RegisterAppInterface"));
    enumStringRepresentationMap.insert(
      std::make_pair(func_id_ns::UnregisterAppInterface,
                     "UnregisterAppInterface"));
    enumStringRepresentationMap.insert(
      std::make_pair(func_id_ns::SetGlobalProperties,
                     "SetGlobalProperties"));

    isInitialized = true;
  }

  return enumStringRepresentationMap;
}

template<>
const std::map<msg_type_ns::eType, std::string> &
NsSmartDeviceLink::NsSmartObjects::TEnumSchemaItem<msg_type_ns::eType>::
getEnumElementsStringRepresentation(void) {
  static bool isInitialized = false;
  static std::map<msg_type_ns::eType, std::string> enumStringRepresentationMap;

  if (false == isInitialized) {
    enumStringRepresentationMap.insert(
      std::make_pair(msg_type_ns::request, "request"));
    enumStringRepresentationMap.insert(
      std::make_pair(msg_type_ns::response, "response"));
    enumStringRepresentationMap.insert(
      std::make_pair(msg_type_ns::notification, "notification"));

    isInitialized = true;
  }

  return enumStringRepresentationMap;
}
}  //namespace NsSmartObjects
}  //namespace NsSmartDeviceLink

int main(int argc, char **argv)
{
  ::testing::InitGoogleMock(&argc, argv);
  return RUN_ALL_TESTS();
}