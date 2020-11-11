FROM nginx:1.18.0 as base

RUN apt-get update
RUN apt-get install nginx-extras -y

RUN mkdir -p /opt/var/cache/nginx && \
    cp -a --parents /usr/lib/nginx /opt && \
    cp -a --parents /usr/share/nginx /opt && \
    cp -a --parents /var/log/nginx /opt && \
    cp -aL --parents /var/run /opt && \
    cp -a --parents /etc/nginx /opt && \
    cp -a --parents /etc/passwd /opt && \
    cp -a --parents /etc/group /opt && \
    cp -a --parents /usr/sbin/nginx /opt && \
    cp -a --parents /lib/x86_64-linux-gnu/libpcre.so.* /opt && \
    cp -a --parents /lib/x86_64-linux-gnu/libz.so.* /opt && \
    cp -a --parents /usr/lib/x86_64-linux-gnu/libperl.so* /opt

RUN cp -a --parents /usr/share/perl/5.28.1 /opt
RUN cp -a --parents /usr/lib/x86_64-linux-gnu/perl5/5.28 /opt
RUN cp -a --parents /usr/share/perl/5.28 /opt

RUN mkdir -p /var/lib/nginx/body

FROM gcr.io/distroless/base-debian10

COPY --from=base /opt /
COPY --from=base /var /
COPY --from=base /var/lib /var
COPY --from=base /var/lib/nginx /var/lib
COPY --from=base /var/lib/nginx/body /var/lib/nginx

COPY nginx/nginx.conf /etc/nginx/nginx.conf
COPY nginx/default.conf /etc/nginx/conf.d/default.conf

COPY html /usr/share/nginx/html

EXPOSE 8080

WORKDIR /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]