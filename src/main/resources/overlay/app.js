function updateStyle(object) {
    let style = document.body.style;
    if (object['gui_scale'])
        style.setProperty('--var_scale', object['gui_scale']);
    if (object['timer_height'])
        style.setProperty('--var_timer_height', object['timer_height'] + 'px');
}

let ws;

function connectWebSocket(handleMessage) {
    ws = new WebSocket('ws://localhost:6180');
    ws.onopen = () => console.log('WS connected');
    ws.onmessage = (ev) => {
        const obj = JSON.parse(ev.data);
        const type = obj['message'] ?? 'unknown';

        handleMessage?.(type, obj);

        if (type === 'update_style')
            updateStyle(obj['style']);
    }
    ws.onclose = () => {
        console.log('WS disconnected, retrying in 2s');
        setTimeout(() => {
            connectWebSocket(handleMessage)
        }, 2000);
    };
    ws.onerror = (e) => console.error(e);
}