// 图片URL处理工具函数
const ImageUtils = {
    /**
     * 获取商品图片URL
     * @param {string} imageUrl - 图片URL
     * @returns {string} 完整的图片URL
     */
    getProductImage(imageUrl) {
        if (!imageUrl) return '/default-product.png';
        // 如果已经是完整URL（http或https开头），直接返回
        if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) return imageUrl;
        // 如果是OSS URL，直接返回
        if (imageUrl.includes('oss') || imageUrl.includes('aliyuncs')) return imageUrl;
        // 否则拼接基础URL
        return API_CONFIG.BASE_URL + imageUrl;
    },
    
    /**
     * 获取头像URL
     * @param {string} avatarUrl - 头像URL
     * @returns {string} 完整的头像URL
     */
    getAvatarUrl(avatarUrl) {
        if (!avatarUrl) return '/default-avatar.png';
        // 如果已经是完整URL，直接返回
        if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) return avatarUrl;
        // 如果是OSS URL，直接返回
        if (avatarUrl.includes('oss') || avatarUrl.includes('aliyuncs')) return avatarUrl;
        // 否则拼接基础URL
        return API_CONFIG.BASE_URL + avatarUrl;
    },
    
    /**
     * 处理图片加载错误
     * @param {Event} event - 错误事件
     * @param {string} defaultImage - 默认图片路径
     */
    handleImageError(event, defaultImage = '/default-product.png') {
        event.target.src = defaultImage;
    }
};
