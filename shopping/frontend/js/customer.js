const { createApp } = Vue;
const { 
    ElContainer, ElHeader, ElMain, ElInput, ElIcon, ElAvatar, ElDropdown, ElDropdownMenu, 
    ElDropdownItem, ElInputNumber, ElRadioGroup, ElRadioButton, ElPagination, ElEmpty, ElMessage 
} = ElementPlus;
const { Search, ShoppingCart, ArrowDown } = ElementPlusIconsVue;

// 检查登录状态
function checkAuth() {
    const token = localStorage.getItem('token');
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    
    if (!token || userInfo.role !== 1) {
        window.location.href = './login.html';
        return null;
    }
    return userInfo;
}

createApp({
    components: {
        ElContainer,
        ElHeader,
        ElMain,
        ElInput,
        ElIcon,
        ElAvatar,
        ElDropdown,
        ElDropdownMenu,
        ElDropdownItem,
        ElInputNumber,
        ElRadioGroup,
        ElRadioButton,
        ElPagination,
        ElEmpty,
        Search,
        ShoppingCart,
        ArrowDown
    },
    data() {
        return {
            userInfo: {},
            searchKeyword: '',
            loading: false,
            products: [],
            cartCount: 0,
            filters: {
                minPrice: null,
                maxPrice: null,
                sort: 'default'
            },
            pagination: {
                page: 1,
                pageSize: 12,
                total: 0
            }
        };
    },
    mounted() {
        this.userInfo = checkAuth();
        if (this.userInfo) {
            this.loadProducts();
            this.loadCartCount();
        }
    },
    methods: {
        async loadProducts() {
            this.loading = true;
            try {
                const params = {
                    page: this.pagination.page,
                    pageSize: this.pagination.pageSize,
                    sort: this.filters.sort
                };
                
                if (this.filters.minPrice !== null && this.filters.minPrice !== '') {
                    params.minPrice = this.filters.minPrice;
                }
                if (this.filters.maxPrice !== null && this.filters.maxPrice !== '') {
                    params.maxPrice = this.filters.maxPrice;
                }
                
                const res = await API.getProducts(params);
                if (res.code === 1 && res.data) {
                    this.products = res.data.list || [];
                    this.pagination.total = res.data.total || 0;
                }
            } catch (error) {
                console.error('加载商品失败:', error);
            } finally {
                this.loading = false;
            }
        },
        
        async loadCartCount() {
            try {
                const res = await API.getCartList();
                if (res.code === 1 && res.data) {
                    const items = res.data || [];
                    this.cartCount = items.reduce((sum, item) => sum + (item.quantity || 0), 0);
                }
            } catch (error) {
                // 忽略错误
            }
        },
        
        handleSearch() {
            if (this.searchKeyword.trim()) {
                window.location.href = `./product-search.html?keyword=${encodeURIComponent(this.searchKeyword)}`;
            }
        },
        
        handlePageChange(page) {
            this.pagination.page = page;
            this.loadProducts();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        },
        
        goToDetail(productId) {
            window.location.href = `./product-detail.html?id=${productId}`;
        },
        
        goToCart() {
            window.location.href = './cart.html';
        },
        
        handleCommand(command) {
            if (command === 'profile') {
                window.location.href = './profile.html';
            } else if (command === 'orders') {
                window.location.href = './orders.html';
            } else if (command === 'logout') {
                this.handleLogout();
            }
        },
        
        getProductImage(imageUrl) {
            if (!imageUrl) return '/default-product.png';
            if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) return imageUrl;
            if (imageUrl.includes('oss') || imageUrl.includes('aliyuncs')) return imageUrl;
            return API_CONFIG.BASE_URL + imageUrl;
        },
        
        getAvatarUrl(avatarUrl) {
            if (!avatarUrl) return '/default-avatar.png';
            if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) return avatarUrl;
            if (avatarUrl.includes('oss') || avatarUrl.includes('aliyuncs')) return avatarUrl;
            return API_CONFIG.BASE_URL + avatarUrl;
        },
        
        handleImageError(event) {
            event.target.src = '/default-product.png';
        },
        
        async handleLogout() {
            try {
                await API.logout();
            } catch (error) {
                // 忽略错误
            }
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
            window.location.href = './index.html';
        }
    }
}).use(ElementPlus).mount('#app');
