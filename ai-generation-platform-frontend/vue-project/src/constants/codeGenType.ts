/** 
  * 代码生成类型枚举 
  */ 
export enum CodeGenTypeEnum {
  HTML = 'html',
  MULTI_FILE = 'multi_file',
  VUE_PROJECT = 'vue_project',
}

/** 
  * 代码生成类型配置 
  */ 
export const CODE_GEN_TYPE_CONFIG = {
  [CodeGenTypeEnum.HTML]: {
    label: '原生 HTML 模式',
    value: CodeGenTypeEnum.HTML,
  },
  [CodeGenTypeEnum.MULTI_FILE]: {
    label: '原生多文件模式',
    value: CodeGenTypeEnum.MULTI_FILE,
  },
  [CodeGenTypeEnum.VUE_PROJECT]: {
    label: 'Vue 项目模式',
    value: CodeGenTypeEnum.VUE_PROJECT,
  },
}

// 生成类型常量，对应后端 CodeGenTypeEnum 枚举
export const CodeGenType = {
  // 原生 HTML 模式
  HTML: CODE_GEN_TYPE_CONFIG[CodeGenTypeEnum.HTML],
  // 原生多文件模式
  MULTI_FILE: CODE_GEN_TYPE_CONFIG[CodeGenTypeEnum.MULTI_FILE],
  // Vue 项目模式
  VUE_PROJECT: CODE_GEN_TYPE_CONFIG[CodeGenTypeEnum.VUE_PROJECT]
}

// 生成类型选项，用于下拉选择
export const CodeGenTypeOptions = [
  CODE_GEN_TYPE_CONFIG[CodeGenTypeEnum.HTML],
  CODE_GEN_TYPE_CONFIG[CodeGenTypeEnum.MULTI_FILE],
  CODE_GEN_TYPE_CONFIG[CodeGenTypeEnum.VUE_PROJECT]
]

// 根据值获取标签
export const getCodeGenTypeLabel = (value: string): string => {
  switch (value) {
    case CodeGenTypeEnum.HTML:
      return CodeGenType.HTML.label
    case CodeGenTypeEnum.MULTI_FILE:
      return CodeGenType.MULTI_FILE.label
    case CodeGenTypeEnum.VUE_PROJECT:
      return CodeGenType.VUE_PROJECT.label
    default:
      return value
  }
}
