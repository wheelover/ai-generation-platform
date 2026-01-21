// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /api/test/hello */
export async function hello(options?: { [key: string]: any }) {
  return request<string>('/api/test/hello', {
    method: 'GET',
    ...(options || {}),
  })
}
