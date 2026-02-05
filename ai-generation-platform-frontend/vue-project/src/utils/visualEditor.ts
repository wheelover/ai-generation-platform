// 可视化编辑相关的工具函数和iframe通信逻辑

// 选中元素的信息类型
export interface SelectedElement {
  tagName: string;
  id: string;
  className: string;
  textContent: string;
  path: string;
}

// 编辑模式状态管理
export interface EditorState {
  isEditing: boolean;
  selectedElement: SelectedElement | null;
}

// 消息类型定义
export enum MessageType {
  ENTER_EDIT_MODE = 'ENTER_EDIT_MODE',
  EXIT_EDIT_MODE = 'EXIT_EDIT_MODE',
  ELEMENT_HOVERED = 'ELEMENT_HOVERED',
  ELEMENT_SELECTED = 'ELEMENT_SELECTED',
  CLEAR_SELECTION = 'CLEAR_SELECTION'
}

// 消息数据结构
export interface EditorMessage {
  type: MessageType;
  data?: any;
}

/**
 * 向iframe发送消息
 * @param iframe iframe元素
 * @param message 消息对象
 */
export const sendMessageToIframe = (
  iframe: HTMLIFrameElement | null,
  message: EditorMessage
): void => {
  if (iframe && iframe.contentWindow) {
    iframe.contentWindow.postMessage(message, '*');
  }
};

/**
 * 从iframe接收消息的处理函数
 * @param event 消息事件
 * @param callback 回调函数，处理接收到的消息
 */
export const handleMessageFromIframe = (
  event: MessageEvent,
  callback: (message: EditorMessage) => void
): void => {
  try {
    const message = event.data as EditorMessage;
    if (message && Object.values(MessageType).includes(message.type)) {
      callback(message);
    }
  } catch (error) {
    console.error('处理iframe消息失败:', error);
  }
};

/**
 * 进入编辑模式
 * @param iframe iframe元素
 */
export const enterEditMode = (iframe: HTMLIFrameElement | null): void => {
  sendMessageToIframe(iframe, {
    type: MessageType.ENTER_EDIT_MODE
  });
};

/**
 * 退出编辑模式
 * @param iframe iframe元素
 */
export const exitEditMode = (iframe: HTMLIFrameElement | null): void => {
  sendMessageToIframe(iframe, {
    type: MessageType.EXIT_EDIT_MODE
  });
};

/**
 * 清除选中元素
 * @param iframe iframe元素
 */
export const clearSelection = (iframe: HTMLIFrameElement | null): void => {
  sendMessageToIframe(iframe, {
    type: MessageType.CLEAR_SELECTION
  });
};

/**
 * 生成元素路径，用于标识元素
 * @param element DOM元素
 * @returns 元素路径字符串
 */
export const generateElementPath = (element: Element): string => {
  const path: string[] = [];
  let current: Element | null = element;
  
  while (current && current.tagName !== 'HTML') {
    const tagName = current.tagName.toLowerCase();
    let selector = tagName;
    
    if (current.id) {
      selector += `#${current.id}`;
    } else if (current.className) {
      selector += `.${current.className.split(' ').filter(c => c).join('.')}`;
    }
    
    path.unshift(selector);
    current = current.parentElement;
  }
  
  return path.join(' > ');
};

/**
 * 格式化选中元素信息，用于显示和提示词
 * @param element 选中的元素信息
 * @returns 格式化后的元素描述
 */
export const formatElementInfo = (element: SelectedElement): string => {
  return `选中元素: ${element.tagName}${element.id ? `#${element.id}` : ''}${element.className ? `.${element.className.split(' ')[0]}` : ''}`;
};
