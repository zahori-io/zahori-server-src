const PROXY_CONFIG = [
    {
        context: [
            "/api",
            "/login",
            "/users/sign-up"
        ],
        target: "http://localhost:9090/zahori",
        secure: false
    }
]

module.exports = PROXY_CONFIG;
