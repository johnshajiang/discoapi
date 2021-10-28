/*
 * Copyright (c) 2021.
 *
 * This file is part of DiscoAPI.
 *
 *     DiscoAPI is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     DiscoAPI is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with DiscoAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.foojay.api.scopes;

import java.util.Arrays;
import java.util.List;


public enum QualityScope implements Scope {
    TCK_TESTED("TCK tested", "tck_tested"),
    AQAVIT_CERTIFIED("AQAVIT certified", "aqavit_certified");

    private final String uiString;
    private final String apiString;


    QualityScope(final String uiString, final String apiString) {
        this.uiString  = uiString;
        this.apiString = apiString;
    }


    @Override public String getUiString() { return uiString; }

    @Override public String getApiString() { return apiString; }


    public static Scope fromText(final String text) {
        switch(text) {
            case "tck_tested":
                return TCK_TESTED;
            case "aqavit_certified":
                return AQAVIT_CERTIFIED;
            default:
                return NOT_FOUND;
        }
    }

    public static List<QualityScope> getAsList() { return Arrays.asList(values()); }
}
