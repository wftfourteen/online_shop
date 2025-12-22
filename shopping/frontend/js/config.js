// API配置
const API_CONFIG = {
    BASE_URL: 'http://localhost:1414',
    TIMEOUT: 10000
};

// 订单状态映射
const ORDER_STATUS = {
    1: { text: '待支付', type: 'warning' },
    2: { text: '已支付', type: 'info' },
    3: { text: '已发货', type: 'success' },
    4: { text: '已完成', type: '' },
    5: { text: '已取消', type: 'danger' }
};

// 用户角色映射
const USER_ROLE = {
    1: '顾客',
    2: '商家'
};

