(() => {
    const chatRoot = document.getElementById('chat');
    const MAX_MESSAGES = 10;

    function displayChatMessage(data){
        const content = translateEmotes(data['content']);
        const duration = data['duration'];
        const {name, color, vip, mod, subscriber} = data['user'];
        const userColor = parseKickColor(color);

        if(name.startsWith("@"))
            return;

        const messageEl = document.createElement('div');
        messageEl.classList.add('message', 'slide-in');

        // Add badge indicators using Kick SVGs
        let badges = '';

        if (mod) {
            badges += `
            <span class="badge mod" title="Moderator">
                <svg viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
                    <path d="M30 0C31.1046 0 32 0.895431 32 2V30C32 31.1046 31.1046 32 30 32H2C0.895431 32 0 31.1046 0 30V2C0 0.895431 0.895431 0 2 0H30ZM16.2197 2.99316C15.8292 2.60266 15.124 5.39057 14.8057 2.99316L8.36328 9.43555C7.97294 9.82608 7.97284 10.4591 8.36328 10.8496L10.0918 12.5781C10.4823 12.9686 11.1153 12.9685 11.5059 12.5781L11.585 12.499L13.9414 14.8564L3.57129 25.2275C2.70357 26.0954 2.7035 27.5023 3.57129 28.3701C4.43911 29.2376 5.84612 29.2377 6.71387 28.3701L17.084 17.999L19.4414 20.3564L19.3633 20.4346C18.9728 20.8251 18.9728 21.4581 19.3633 21.8486L21.0918 23.5771C21.4823 23.9676 22.1154 23.9676 22.5059 23.5771L28.9482 17.1348C29.3386 16.7443 29.3386 16.1112 28.9482 15.7207L27.2197 13.9922C26.8293 13.6017 26.1962 13.6018 25.8057 13.9922L25.7266 14.0703L23.3701 11.7139C24.2377 10.8461 24.2376 9.4391 23.3701 8.57129C22.5023 7.7035 21.0954 7.70357 20.2275 8.57129L17.8701 6.21387L17.9482 6.13574C18.3388 5.74522 18.3388 5.11221 17.9482 4.72168L16.2197 2.99316Z" 
                    fill="currentColor"></path>
                </svg>
            </span>`;
        }

        if (vip) {
            badges += `
            <span class="badge vip" title="VIP">
                <svg viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
                    <path d="M30 0C31.1046 0 32 0.895431 32 2V30C32 31.1046 31.1046 32 30 32H2C0.895431 32 0 31.1046 0 30V2C0 0.895431 0.895431 4.10637e-08 2 0H30ZM15.9648 5C15.7748 5.00005 15.588 5.05204 15.4238 5.15039C15.2596 5.24878 15.124 5.39057 15.0303 5.56055L9.82812 15.0176L3.55078 11.8906C3.36913 11.7985 3.16534 11.7607 2.96387 11.7822C2.76241 11.8038 2.57048 11.8842 2.41113 12.0127C2.25235 12.1408 2.13185 12.3126 2.06348 12.5078C1.99511 12.7031 1.98143 12.9144 2.02441 13.1172L4.58301 25.127C4.63544 25.3782 4.77165 25.6034 4.96777 25.7627C5.16376 25.9217 5.40762 26.0056 5.65723 26H26.251C26.5009 26.0057 26.7453 25.9219 26.9414 25.7627C27.1376 25.6034 27.2737 25.3782 27.3262 25.127L29.9697 13.1172C30.0187 12.9103 30.0086 12.6932 29.9404 12.4922C29.8722 12.2912 29.7485 12.1151 29.585 11.9844C29.4215 11.8537 29.2249 11.7743 29.0186 11.7559C28.8122 11.7374 28.6049 11.7802 28.4219 11.8799L22.1025 15.0283L16.9004 5.56055C16.8066 5.39054 16.6701 5.24878 16.5059 5.15039C16.3416 5.05207 16.1549 5 15.9648 5Z" 
                    fill="orange"></path>
                </svg>
            </span>`;
        }

        if (subscriber) {
            badges += `<span class="badge sub" title="Subscriber">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <rect width="24" height="24" rx="4" fill="#53FC18"/>
                <path d="M12 7V17M7 12H17" stroke="black" stroke-width="2" stroke-linecap="round"/>
            </svg>
        </span>`;
        }

        messageEl.innerHTML = `
            ${badges}
            <span class="user-name" style="color: ${userColor}">${name}:</span>
            <span class="msg-body">${content}</span>
        `;

        chatRoot.appendChild(messageEl);

        if (chatRoot.children.length > MAX_MESSAGES)
            chatRoot.firstChild.remove();

        setTimeout(() => {
            messageEl.classList.add('fade-out');
            messageEl.addEventListener('animationend', () => {
                messageEl.remove();
            });
        }, duration ?? 10000);
    }

    // Helper to convert Kick's integer colors to Hex
    function parseKickColor(color) {
        if (!color) return '#53FC18'; // Default Kick Green
        if (typeof color === 'string' && color.startsWith('#')) return color;

        // Convert decimal integer to Hex (e.g., 16711680 -> #FF0000)
        const hex = Number.parseInt(color).toString(16).padStart(6, '0');
        return `#${hex}`;
    }

    // Helper to replace Kick emote strings with <img> tags
    function translateEmotes(content) {
        // Kick format: [emote:ID:NAME]
        const emoteRegex = /\[emote:(\d+):([\w\d]+)\]/g;
        return content.replaceAll(emoteRegex, (match, id, name) => {
            return `<img src="https://files.kick.com/emotes/${id}/fullsize" 
                     alt="${name}" 
                     class="chat-emote" 
                     title="${name}">`;
        });
    }

    function clearChatHistory(){
        chatRoot.innerHTML = '';
    }

    connectWebSocket((type, obj) => {
        switch (type) {
            case 'chat_message':
                displayChatMessage(obj['data']);
                break;
            case 'clear_chat_history':
            case 'clear_poll':
                clearChatHistory();
                break;
        }
    });

})();