package de.hendrikjanssen.geotifftools.metadata.geokeys.values;

public enum LinearUnit {

    Meter(9001),
    Foot(9002),
    FootUS_Survey(9003),
    FootModified_American(9004),
    FootClarke(9005),
    FootIndian(9006),
    Link(9007),
    LinkBenoit(9008),
    LinkSears(9009),
    ChainBenoit(9010),
    ChainSears(9011),
    YardSears(9012),
    YardIndian(9013),
    Fathom(9014),
    MileInternationalNautical(9015);

    private final int code;

    LinearUnit(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }
}
