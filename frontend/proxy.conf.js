const PROXY_CONFIG = [
    {
        context: [
            "/zahori/api",
            "/zahori/login",
            "/zahori/users/sign-up"
        ],
        target: "http://localhost:9090",
        secure: false
    }
]

module.exports = PROXY_CONFIG;
