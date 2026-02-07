import { CodeGenTypeEnum } from '@/constants/codeGenType'

// 静态资源基础 URL
const STATIC_BASE_URL = import.meta.env.VITE_PREVIEW_BASE_URL || import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api/static'

/**
 * 获取静态资源预览URL
 * @param codeGenType 代码生成类型
 * @param appId 应用ID
 * @returns 预览URL
 */
export const getStaticPreviewUrl = (codeGenType: string, appId: string) => {
  const baseUrl = `${STATIC_BASE_URL}/${codeGenType}_${appId}/`
  // 如果是 Vue 项目，浏览地址需要添加 dist 后缀
  if (codeGenType === CodeGenTypeEnum.VUE_PROJECT) {
    return `${baseUrl}dist/index.html`
  }
  return baseUrl
}
