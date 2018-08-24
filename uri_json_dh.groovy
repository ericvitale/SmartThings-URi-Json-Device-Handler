/*
* Author: N Vass
*
* Json Switch Device Handler
*/

public static String version() { return "v0.1.0.20180824" }

include 'asynchttp_v1'

preferences {
    
	section("Device Settings"){
		input "internal_ip", "text", title: "Internal IP", required: true
		input "internal_port", "number", title: "Internal Port", required: true, default: 80, range: "0..65535"
		input "device_relay", "text", title: "Relay #", required: true, default: "1"
	}
    
    section("Commands"){
		input "on_command", "text", title: "On Json", required: true, default: "on"
		input "off_command", "text", title: "Off Json", required: true, default: "off"
	}
    
    section("Logging") {
        	input "logging", "enum", title: "Log Level", required: false, defaultValue: "INFO", options: ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"]
	}	
}

metadata {
	definition (name: "Json Switch", namespace: "NVass", author: "N Vass") {
		capability "Actuator"
		capability "Switch"		
	}

	tiles {
		standardTile("button", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "on"
				state "on", label: 'On', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00a0dc", nextState: "off"
		}
		standardTile("offButton", "device.switch", width: 1, height: 1, canChangeIcon: true) {
			state "default", label: 'Force Off', action: "switch.off", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
		standardTile("onButton", "device.switch", width: 1, height: 1, canChangeIcon: true) {
			state "default", label: 'Force On', action: "switch.on", icon: "st.switches.switch.on", backgroundColor: "#00a0dc"
		}
		main "button"
			details (["button","onButton","offButton"])
	}
}

def updated() {
	setIpAddress(internal_ip)
    log("IP Address set to ${getIpAddress()}.", "INFO")
    setPort(internal_port)
    log("Port set to ${getPort()}.", "INFO")
	setRelay(device_relay)
    log("Relay set to ${getRelay()}.", "INFO")
    setOnCommand(on_command)
    log("On command set to ${getOnCommand()}.", "INFO")
    setOffCommand(off_command)
    log("Off command set to ${getOffCommand()}.", "INFO")
}

def setIpAddress(value) {
	state.deviceIP = value
}

def getIpAddress() {
	return state.deviceIP
}

def setPort(value) {
	state.thePort = value
}

def getPort() {
	return state.thePort
}

def setRelay(value) {
	state.theRelay = value
}

def getRelay() {
	return state.theRelay
}

def setOnCommand(value) {
	state.onCommand = value
}

def getOnCommand() {
	return state.onCommand
}

def setOffCommand(value) {
	state.offCommand = value
}

def getOffCommand() {
	return state.offCommand
}

def parse(String description) {
	log.debug(description)
}

def on() {
		sendCommand(getOnCommand())
        sendEvent(name: "switch", value: "on") 
		log("Sending ${getOnCommand()} as part of the on event.", "INFO")
}

def off() {
		sendCommand(getOffCommand())
		sendEvent(name: "switch", value: "off")
		log("Sending ${getOffCommand()} as part of the off event.", "INFO")
}

def sendCommand(theCommandValue) {
	def commands =  ["${getRelay()}": "${theCommandValue}"]
	
	def params = [
				 	uri: 'http://api.github.com',
					headers: [	"Content-Type":"application/json", 
        		  				"Accept":"application/json"],
				  	body: commands
				 ]
    
    log("Params = ${params}", "DEBUG")
		
	asynchttp_v1.post('postResponseHandler', params)
}

def postResponseHandler(response, data) {

    if(response.getStatus() == 200 || response.getStatus() == 207) {
	log.info "POST response received from the device."
    } else {
        log.error "POST Error: ${response.getErrorData()}"
    }
}

private getLogPrefix() {
	return "uri_switch.${version()}.${device.label}>>>"
}

private determineLogLevel(data) {
    switch (data?.toUpperCase()) {
        case "TRACE":
            return 0
            break
        case "DEBUG":
            return 1
            break
        case "INFO":
            return 2
            break
        case "WARN":
            return 3
            break
        case "ERROR":
        	return 4
            break
        default:
            return 1
    }
}

def log(data, type) {
    data = "${getLogPrefix()} ${data ?: ''}"
        
    if (determineLogLevel(type) >= determineLogLevel(settings?.logging ?: "INFO")) {
        switch (type?.toUpperCase()) {
            case "TRACE":
                log.trace "${data}"
                break
            case "DEBUG":
                log.debug "${data}"
                break
            case "INFO":
                log.info "${data}"
                break
            case "WARN":
                log.warn "${data}"
                break
            case "ERROR":
                log.error "${data}"
                break
            default:
                log.error "${getLogPrefix()} Invalid Log Setting"
        }
    }
}
