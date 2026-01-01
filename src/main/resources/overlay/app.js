(() => {
    const poll = document.getElementById('poll');

    function setPoll(object) {
        // object: {"pollId":5,"entries":["Weak Legs","Half Heart","Force W","Random Event"]}
        poll.innerHTML = '';
        if (Object.keys(object).length === 0) {
            poll.textContent = 'No active poll';
            return;
        }
        const title = document.createElement('p');
        title.classList.add('poll-title');
        title.innerHTML = `Total votes: <span id="total-votes">0</span>`;
        poll.appendChild(title);
        poll.innerHTML += `<div class="title-separator"></div>`;
        // entries in format: ["Weak Legs","Half Heart","Force W","Random Event"]
        // section for each entry with entry voteID, entry name, entry votes
        const entriesDiv = document.createElement('div');
        entriesDiv.id = 'poll-entries';
        for (let i = 0; i < object['entries'].length; i++) {
            entriesDiv.innerHTML += `<div class="poll-entry" id="entry-${i}"><span class="entry-id">${i + (object['pollId'] % 2 === 0 ? 1 : 5)}.</span><span class="entry-name">${object['entries'][i]}</span><span class="entry-votes" id="votes-${i}">0</span></div>`;
        }
        poll.appendChild(entriesDiv);
    }

    function updateVotes(object){
        let totalVotes = object['totalVotes'];
        let votes = object['votes']; // jsonarray of votes of indexes 0-3
        console.log(totalVotes);
        console.log(votes);
        document.getElementById('total-votes').innerHTML = totalVotes;

        for (let i = 0; i < object['votes'].length; i++) {
            let vote = totalVotes === 0 ? 0 : (votes[i] / totalVotes);
            let percent = Math.round(vote * 100);

            let el = document.getElementById(`entry-${i}`);
            el.style.setProperty('--percentage', percent + "%");
            document.getElementById(`votes-${i}`).textContent = percent;
        }
    }

    let ws;

    function updateStyle(object) {
        let style = document.body.style;
        if (object['gui_scale'])
            style.setProperty('--var_scale', object['gui_scale']);
        if (object['timer_height'])
            style.setProperty('--var_timer_height', object['timer_height'] + 'px');
    }

    function connect() {
        ws = new WebSocket('ws://localhost:6180');
        poll.innerHTML = 'Connecting...';
        ws.onopen = () => console.log('WS connected');
        ws.onmessage = (ev) => {
            const obj = JSON.parse(ev.data);
            const type = obj['message'] ?? 'unknown';
            switch (type) {
                case 'new_poll':
                    setPoll(obj['poll']);
                    break;
                case 'clear_poll':
                    setPoll({});
                    break;
                case 'update_style':
                    updateStyle(obj['style']);
                    break;
                case 'poll_votes':
                    updateVotes(obj['data']);
                    break;
                default:
                    console.warn('Unknown message type: ' + type);
            }
        }
        ws.onclose = () => {
            console.log('WS disconnected, retrying in 2s');
            setTimeout(connect, 2000);
        };
        ws.onerror = (e) => console.error(e);
    }

    connect();
})();