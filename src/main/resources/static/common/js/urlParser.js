/**
 * 
 */

function objectToQueryString(obj) {
			var qs = "?";
			for ( var prop in obj) {
				qs += prop + "=" + obj[prop] + "&";
			}

			return qs.slice(0, -1);
}

function queryStringToObject(queryString) {
	if (queryString.length == 0) {
		return {};
	}
	var paramObj = {};
	var params = queryString.substr(1).split("&");
	params.forEach(function(param) {
		i = param.indexOf("=");
		paramObj[param.substr(0, i)] = param.substr(i + 1);
	});
	return paramObj;
}