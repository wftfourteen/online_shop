const { createApp } = Vue;
const { ElButton, ElCard, ElForm, ElFormItem, ElInput, ElRadioGroup, ElRadioButton, ElMessage } = ElementPlus;
const { User, Lock, Message } = ElementPlusIconsVue;

const validateConfirmPassword = (rule, value, callback) => {
    const app = getCurrentInstance();
    if (!value) {
        callback(new Error('请再次输入密码'));
    } else if (value !== app.ctx.registerForm.password) {
        callback(new Error('两次输入密码不一致'));
    } else {
        callback();
    }
};

createApp({
    components: {
        ElButton,
        ElCard,
        ElForm,
        ElFormItem,
        ElInput,
        ElRadioGroup,
        ElRadioButton,
        ElIcon: ElementPlus.ElIcon,
        User,
        Lock,
        Message
    },
    data() {
        return {
            loading: false,
            registerForm: {
                username: '',
                email: '',
                password: '',
                confirmPassword: '',
                role: 1 // 1-顾客, 2-商家
            },
            rules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },
                    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
                ],
                email: [
                    { required: true, message: '请输入邮箱', trigger: 'blur' },
                    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
                ],
                confirmPassword: [
                    { required: true, message: '请再次输入密码', trigger: 'blur' },
                    { validator: (rule, value, callback) => {
                        if (!value) {
                            callback(new Error('请再次输入密码'));
                        } else if (value !== this.registerForm.password) {
                            callback(new Error('两次输入密码不一致'));
                        } else {
                            callback();
                        }
                    }, trigger: 'blur' }
                ],
                role: [
                    { required: true, message: '请选择身份', trigger: 'change' }
                ]
            }
        };
    },
    methods: {
        async checkUsername() {
            if (!this.registerForm.username || this.registerForm.username.length < 3) {
                return;
            }
            try {
                const res = await API.checkUsername(this.registerForm.username);
                if (res.code === 0) {
                    ElMessage.warning('用户名已被占用');
                }
            } catch (error) {
                // 用户名可用或检查失败，不处理
            }
        },
        
        async checkEmail() {
            if (!this.registerForm.email) {
                return;
            }
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(this.registerForm.email)) {
                return;
            }
            try {
                const res = await API.checkEmail(this.registerForm.email);
                if (res.code === 0) {
                    ElMessage.warning('邮箱已被注册');
                }
            } catch (error) {
                // 邮箱可用或检查失败，不处理
            }
        },
        
        async handleRegister() {
            try {
                await this.$refs.registerFormRef.validate();
                this.loading = true;
                
                const registerData = {
                    username: this.registerForm.username,
                    email: this.registerForm.email,
                    password: this.registerForm.password,
                    role: this.registerForm.role
                };
                
                const res = await API.register(registerData);
                
                if (res.code === 1) {
                    ElMessage.success('注册成功，请登录');
                    setTimeout(() => {
                        window.location.href = './login.html';
                    }, 1500);
                } else {
                    ElMessage.error(res.msg || '注册失败');
                    this.loading = false;
                }
            } catch (error) {
                this.loading = false;
                console.error('注册错误:', error);
            }
        }
    }
}).use(ElementPlus).mount('#app');
