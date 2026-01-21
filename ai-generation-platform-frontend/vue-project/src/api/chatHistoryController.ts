// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 POST /chatHistory/admin/delete */
export async function deleteChatHistory(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/chatHistory/admin/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /chatHistory/admin/list/page */
export async function listChatHistoryByPage(
  body: API.ChatHistoryQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageChatHistoryVO>('/chatHistory/admin/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /chatHistory/admin/list/page/vo */
export async function listAllChatHistoryByPageForAdmin(
  body: API.ChatHistoryQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageChatHistory>('/chatHistory/admin/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /chatHistory/app/${param0} */
export async function listAppChatHistory(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listAppChatHistoryParams,
  options?: { [key: string]: any }
) {
  const { appId: param0, ...queryParams } = params
  return request<API.BaseResponsePageChatHistory>(`/chatHistory/app/${param0}`, {
    method: 'GET',
    params: {
      // pageSize has a default value: 10
      pageSize: '10',
      ...queryParams,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /chatHistory/delete/app */
export async function deleteAppChatHistory(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/chatHistory/delete/app', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /chatHistory/latest */
export async function getLatestChatHistory(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getLatestChatHistoryParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListChatHistoryVO>('/chatHistory/latest', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /chatHistory/more */
export async function getMoreChatHistory(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getMoreChatHistoryParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListChatHistoryVO>('/chatHistory/more', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
